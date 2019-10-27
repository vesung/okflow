package com.github.vesung.flow.service.impl;

import com.github.vesung.flow.FlowException;
import com.github.vesung.flow.IFlowUser;
import com.github.vesung.flow.persistence.dao.FlowDataMapper;
import com.github.vesung.flow.persistence.dao.FlowDefMapper;
import com.github.vesung.flow.persistence.dao.FlowLogMapper;
import com.github.vesung.flow.persistence.model.FlowData;
import com.github.vesung.flow.persistence.model.FlowLog;
import com.github.vesung.flow.service.*;
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
    private UserFindService userService;
    @Resource
    private FlowDataMapper flowDataMapper;

    @Override
    public Flow start(String flowType, String buzId, IFlowUser optionUser, String flowDept) {

        int buzCount = flowDataMapper.selectCount(new FlowData().setFlow_type(flowType).setBuz_id(buzId));
        if(buzCount >= 1){
            throw new FlowException("流程已存在，不要重复启动");
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
    public int countWaitingfor(String userAccount) {
        return this.flowDataMapper.selectCount(new FlowData().setCurrent_user(userAccount));
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
        example.createCriteria()
                .andIsNotNull("comments")
                .andEqualTo("flow_type", flowType)
                .andIn("biz_id", buzIds)
                .andIn("flow_action", actions);
        example.setOrderByClause("update_date desc");

        return flowLogMapper.selectByExample(example);
    }


}
