package com.github.vesung.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.vesung.flow.FlowException;
import com.github.vesung.flow.FlowUser;
import com.github.vesung.flow.bean.FlowNextStep;
import com.github.vesung.flow.persistence.dao.FlowDataMapper;
import com.github.vesung.flow.persistence.dao.FlowDefMapper;
import com.github.vesung.flow.persistence.dao.FlowLogMapper;
import com.github.vesung.flow.persistence.dao.FlowTypeDefMapper;
import com.github.vesung.flow.persistence.model.FlowData;
import com.github.vesung.flow.persistence.model.FlowStep;
import com.github.vesung.flow.persistence.model.FlowLog;
import com.github.vesung.flow.persistence.model.FlowTypeDef;
import com.github.vesung.flow.service.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 流程管理服务
 *
 * @author wangjing.dc@qq.com
 * @Date 2019-05-11 17:06:29
 */
@Service
public class FlowServiceImpl implements FlowService {

    private static final Logger log = LoggerFactory.getLogger(FlowServiceImpl.class);

    private static final String const_draft = "draft";
    private static final String const_creator = "creator";
    private static final String const_endFlow = "-2";

    @Resource
    private FlowLogMapper flowLogMapper;
    @Resource
    private FlowDefMapper flowDefMapper;
    @Resource
    private FlowUserService userService;
    @Resource
    private FlowDataMapper flowDataMapper;
    @Resource
    private FlowTypeDefMapper flowTypeDefMapper;


    @Override
    public List<FlowTypeDef> listFlowType(){
        List<FlowTypeDef> types = flowTypeDefMapper.selectAll();
        return types;
    }

    @Override
    public Flow start(String flowType, String buzId, FlowUser optionUser, String flowDept) {

        int buzCount = flowDataMapper.selectCount(new FlowData().setFlow_type(flowType).setBuz_id(buzId));
        if(buzCount >= 1){
            log.info("流程已存在..........................");
            return this.getCurrentFlow(flowType, buzId);
        }

        // 更新流程主单信息
        FlowData flowData = new FlowData();
        flowData.setFlow_type(flowType);
        flowData.setBuz_id(buzId);
        flowData.setStatus(const_draft);
        flowData.setUpdate_user(optionUser.getAccount());
        flowData.setUpdate_date(new Date());
        flowData.setCurrent_user(optionUser.getAccount());
        flowData.setCurrent_role(optionUser.getRoleCode());
        flowData.setCurrent_dept(optionUser.getDeptCode());
        flowData.setStart_date(new Date());
        flowData.setFlow_dept(flowDept);
        flowData.setCreator(optionUser.getAccount());

        flowDataMapper.insert(flowData);
        return this.getCurrentFlow(flowType, flowData.getBuz_id());
    }

    public FlowData findFlowData(String flowType, String buzId) {
        return flowDataMapper.selectOne(new FlowData().setFlow_type(flowType).setBuz_id(buzId));
    }

    @Lookup
    public Flow newFlow(FlowData flowData){
        return null;
    }

    @Override
    public Flow getCurrentFlow(String flowType, String buzId) {
        FlowData flowData = this.flowDataMapper.selectOne(new FlowData()
                .setFlow_type(flowType)
                .setBuz_id(buzId));

        if(flowData == null){
            throw new FlowException(String.format("流程不存在type=%s,buzId=%s", flowType,  buzId));
        }

        return this.newFlow(flowData);
    }

    @Override
    public int countWaitingfor(String flowType, String userAccount, String roleCode) {
        return this.flowDataMapper.selectCount(new FlowData()
                .setCurrent_user(userAccount)
                .setCurrent_role(roleCode)
                .setFlow_type(flowType)
        );
    }


    /**
     * 获取指定条件的日志列表
     * @param buzIds
     * @param actions
     * @return
     */
    @Override
    public List<FlowLog> queryFlowLogsByFilter(String flowType, List<String> buzIds, List<String> actions){
        Example example = new Example(FlowLog.class);
        Example.Criteria criteria = example.createCriteria()
                .andIsNotNull("comments")
                .andEqualTo("flow_type", flowType)
                .andIn("biz_id", buzIds);
        if(actions != null){
            criteria.andIn("flow_action", actions);
        }
        example.setOrderByClause("update_date desc");

        return flowLogMapper.selectByExample(example);
    }

    @Override
    public void updateStep(FlowStep flow) {
        FlowStep def = this.assertNotNull(flow.getId());
        def.setStep(flow.getStep());
        def.setStep_name(flow.getStep_name());
        def.setStep_role(flow.getStep_role());
        def.setClass_path(flow.getClass_path());
        def.setUpdate_user(userService.currentUser().getAccount());
        def.setUpdate_date(new Date());

        flowDefMapper.updateByPrimaryKey(def);
    }

    @Override
    public void addStep(FlowStep flow) {
        if(Strings.isNullOrEmpty(flow.getType())){
            throw new FlowException("请选择流程种类");
        }

        List<FlowStep> def = flowDefMapper.select(new FlowStep()
                .setStep(flow.getStep())
                .setType(flow.getType())
        );
        if(def != null && def.size() > 0){
            throw new FlowException("流程步骤已存在");
        }

        flow.setId(null);
        flow.setNext_step(null);
        flowDefMapper.insert(flow);
    }

    @Override
    public FlowStep queryStepById(Integer flowId) {
        FlowStep flow = flowDefMapper.selectByPrimaryKey(flowId);
        return flow;
    }

    @Override
    public void deleteStep(Integer flowId) {
        flowDefMapper.deleteByPrimaryKey(flowId);

    }

    @Override
    public List<FlowNextStep> queryNextStepDef(Integer stepId) {
        FlowStep step = this.assertNotNull(stepId);
        String nextStr = step.getNext_step();
        if(nextStr == null){
            return null;
        }

        List<FlowNextStep> ret = new ArrayList<>();

        if(nextStr.startsWith("{")){
            Map<String, String> map = JSON.parseObject(nextStr, Map.class);
            map.forEach((key, value) ->{
                ret.add(new FlowNextStep().setAction(key).setNext(value));
            });
        }else{
            ret.add(new FlowNextStep().setAction("default").setNext(nextStr));
        }

        return ret;
    }

    @Override
    public void updateNextStep(Integer stepId, Map<String, String> nexts) {
        FlowStep step = this.assertNotNull(stepId);
        step.setNext_step(JSON.toJSONString(nexts));
        step.setUpdate_date(new Date());
        step.setUpdate_user(userService.currentUser().getAccount());

        flowDefMapper.updateByPrimaryKey(step);
    }


    private FlowStep assertNotNull(Integer stepId) {
        FlowStep flow = flowDefMapper.selectByPrimaryKey(stepId);
        if(flow == null){
            throw new FlowException("步骤未定义");
        }
        return flow;
    }

}
