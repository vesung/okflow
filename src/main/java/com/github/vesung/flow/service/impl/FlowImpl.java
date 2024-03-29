package com.github.vesung.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.vesung.common.ListBuilder;
import com.github.vesung.common.MapBuilder;
import com.github.vesung.flow.FlowException;
import com.github.vesung.flow.FlowUser;
import com.github.vesung.flow.event.FlowSubmitEvent;
import com.github.vesung.flow.persistence.dao.FlowDataMapper;
import com.github.vesung.flow.persistence.dao.FlowDefMapper;
import com.github.vesung.flow.persistence.dao.FlowLogMapper;
import com.github.vesung.flow.persistence.model.FlowData;
import com.github.vesung.flow.persistence.model.FlowStep;
import com.github.vesung.flow.persistence.model.FlowLog;
import com.github.vesung.flow.service.*;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/6/16
 */
@Service
@Scope("prototype")
public class FlowImpl implements Flow {

    private static final Logger log = LoggerFactory.getLogger(FlowImpl.class);

    private static final String const_draft = "draft";
    private static final String const_end = "end";
    private static final String const_creator = "creator";
    private static final String const_endFlow = "-2";
    private static final String const_transfer = "_transfer";
    private static final String const_action = "_action";
    private static final String const_step = "_step";
    private static final String const_data = "_data";

    private final FlowData flowData;

    @Resource
    private FlowDataMapper flowDataMapper;
    @Resource
    private FlowUserService userService;
    @Resource
    private FlowService flowService;
    @Resource
    private FlowDefMapper flowDefMapper;
    @Resource
    private FlowLogMapper flowLogMapper;
    @Resource
    private ApplicationContext applicationContext;

    private FlowTimeHelper flowTimeHelper = null;

    public FlowImpl(FlowData flowData){
        this.flowData = flowData;

    }

    /**
     * 设置流程附加数据
     * @param varName
     * @param varValue
     * @return
     */
    public FlowImpl setVar(String varName, Object varValue) {
        String vars = flowData.getVars();
        Map<String, Object> varMap;
        if(Strings.isNullOrEmpty(vars)){
            varMap = new HashMap<>();
        }else{
            varMap = JSON.parseObject(vars, HashMap.class);
        }
        varMap.put(varName, varValue);

        this.flowDataMapper.updateByPrimaryKey(flowData.setVars(JSON.toJSONString(varMap)));
        return this;
    }

    /**
     * 查找当前流程下一步处理人列表
     * @param action
     * @return
     */
    public List<FlowUser> findNextUsers(String action) {

        // 流程已结束
        if(const_endFlow.equals(flowData.getCurrent_step())){
            throw new FlowException("流程已结束");
        }

        FlowStep nextStep = this.findNextStep(action);
        if(nextStep == null) {
            log.error("下一步流程未定义:" + action);
            return null;
        }

        // 当前是最后流程最后一步，没有下一步联系人
        if(const_endFlow.equals(nextStep.getStep())){
            return new ListBuilder<FlowUser>().add(new FlowUser.block()).build();
        }

        // 下一步处理人：申请人
        String nextRole = nextStep.getStep_role();
        if(const_creator.equals(nextRole)){
            List<FlowUser> nextUser = new ArrayList<>();
            nextUser.add(userService.findUserByAccount(flowData.getCreator()));
            return nextUser;
        }

        List<FlowUser> next =  userService.findUsers(flowData.getFlow_dept(), nextStep.getStep_role());
        if(next == null || next.size() < 1)
            log.error(String.format("未找到下一步处理人,dept=%s,role=%s", flowData.getFlow_dept(), nextStep.getStep_role()));

        return next;
    }

    /**
     * 查找下一步定义
     * @param action
     * @return
     */
    public FlowStep findNextStep(String action) {
        // 当前流程
        FlowStep step = this.findCurrentStep();

        if(const_draft.equals(flowData.getStatus())){
            return step;
        }

        // 特殊处理类
        if(!Strings.isNullOrEmpty(step.getClass_path())){
            Object obj = OkflowSpringContextHolder.getBean(step.getClass_path());
            if(obj instanceof NextStepFinder){
                return ((NextStepFinder)obj).findNextStep(this, action);
            }
        }

        String nextStep = step.getNext_step();
        if(nextStep == null || "".equals(nextStep)){
            // 流程结束
            return null;
        }

        // 下一步为forward配置
        if(nextStep.startsWith("{")){
            JSONObject forward = JSON.parseObject(nextStep);
            nextStep = forward.getString(action);
            // 尝试获取默认步骤
            if(nextStep == null){
                nextStep = forward.getString("default");
            }
        }

        if(nextStep == null){
            return null;
        }

        return flowDefMapper.selectOne(new FlowStep()
                .setType(flowData.getFlow_type())
                .setStep(nextStep));
    }

    /**
     * 查找当前流程
     * @return
     */
    public FlowStep findCurrentStep() {
        return flowDefMapper.selectOne(new FlowStep()
                .setType(flowData.getFlow_type())
                .setStep(const_draft.equals(flowData.getStatus()) ? "0" : flowData.getCurrent_step()));
    }

    /**
     * 提交当前流程
     * @param action
     * @param currUser
     * @param nextUser
     * @param comments
     * @param data
     * @return
     */
    @Transactional
    public Flow submitTask(String action, FlowUser currUser, FlowUser nextUser, String comments, String data) {
        if(flowData == null){
            throw new FlowException("流程不存在");
        }
        if(const_endFlow.equals(flowData.getCurrent_step())){
            throw new FlowException("流程已结束，请勿重复提交");
        }

        if(! this.flowData.getCurrent_user().equals(currUser.getAccount())){
            throw new FlowException("无权操作当前流程");
        }

        FlowStep currStep = this.findCurrentStep();
        FlowStep nextStep = this.findNextStep(action);

        if(nextStep == null){
            throw new FlowException("下一个步骤未定义:" + action);
        }

        // 保存当前步骤
        this.setStepVar(currStep.getStep(), const_action, action);

        if(data != null){
            this.setStepVar(currStep.getStep(), const_data, data);
        }

        String lastAction = (String) this.getVar("_lastAction");
        if(lastAction != null){
            flowData.setLast_action(lastAction);
        }
        this.setVar("_lastAction", action);
        // 当前流程即将结束，如果处于流程转报过程，则恢复转报前流程
        if(const_endFlow.equals(nextStep.getStep()) && this.isTransfering()){
            FlowStep transferStep = this.unTransferDept();
            FlowUser transferUser = this.userService.findUsers(
                    this.flowData.getFlow_dept(), transferStep.getStep_role()).get(0);
            this.goNextStep(flowData, currStep, transferStep, currUser, transferUser,
                    action, comments);
            log.info("流程切换回转报前机构");
        }else{
            this.goNextStep(flowData,
                    currStep, nextStep,
                    currUser, nextUser, action, comments);
            log.info("流程结束");
        }

        // 发布流程提交事件
        try{
            applicationContext.publishEvent(new FlowSubmitEvent(this, flowData));
        }catch (Exception e){
            log.error("发布流程事件异常", e);
        }

        return this;
    }

    /**
     * 保存步骤变量
     * @param stepName
     * @param varName
     * @param varValue
     */
    private void setStepVar(String stepName, String varName, String varValue) {
        Map<String, Object> var = (Map<String, Object>) this.getVar(const_step+ stepName);
        if(var == null){
            var = new HashMap<>();
        }
        var.put(varName, varValue);

        this.setVar(const_step + stepName, var);
    }

    /**
     * 判断当前路程是否处于机构转报中
     * @return
     */
    @Override
    public boolean isTransfering() {
        Object obj = this.getVar(const_transfer);
        return obj != null && obj instanceof List && ((List)obj).size() > 0;
    }

    /**
     * 获取当前流程变量
     * @param action
     * @return
     */
    public Object getVar(String action) {
        String vars = this.flowData.getVars();
        if(vars == null)
            return null;

        Map varMap = JSON.parseObject(vars, Map.class);
        return varMap.get(action);
    }

    /**
     *
     * @return
     */
    public String getCurrentDept() {
        return this.flowData.getFlow_dept();
    }

    /**
     * 切换流程机构
     * @param destDept
     */
    @Transactional
    public Flow transferDept(String destDept) {
        if(flowData.getFlow_dept().equals(destDept)){
            throw new FlowException("流程已经在切换机构了，请勿重复操作");
        }

        // 备份当前机构当前步骤
        Map<String, String> currValue = new MapBuilder<String, String>()
                .put("currFlowDept", flowData.getFlow_dept())
                .put("currStep", flowData.getCurrent_step())
                .put("currRole", flowData.getCurrent_role())
                .put("currUser", flowData.getCurrent_user())
                .build();

        List<Map<String, String>> transferInfo;
        Object obj = this.getVar(const_transfer);
        if(obj == null){
            transferInfo = new ArrayList<>();
        }else{
            transferInfo = (List<Map<String, String>>) obj;
        }
        transferInfo.add(currValue);
        this.setVar(const_transfer, transferInfo);

        // 更新当前流程到新机构
        this.flowDataMapper.updateByPrimaryKey(flowData.setFlow_dept(destDept));

        return this;
    }

    /**
     * 恢复切换的流程机构
     * @return
     */
    private FlowStep unTransferDept() {
        List depts = (List) this.getVar(const_transfer);
        Map<String, String> dept = (Map<String, String>) depts.get(0);

        // 恢复流程机构
        String flowDept = dept.get("currFlowDept");
        this.flowDataMapper.updateByPrimaryKey(this.flowData.setFlow_dept(flowDept));

        // 更新流程机构数据
        depts.remove(0);
        this.setVar(const_transfer, depts);

        // 返回切换前步骤
        return this.flowDefMapper.selectOne(new FlowStep()
            .setStep(dept.get("currStep"))
            .setType(this.flowData.getFlow_type()));
    }

    /**
     * 查找指定流程定义
     * @param stepName
     * @return
     */
    public FlowStep findStepByName(String stepName) {
        return this.flowDefMapper.selectOne(new FlowStep()
            .setType(this.flowData.getFlow_type())
            .setStep(stepName));
    }

    /**
     * 获取当前流程日志列表
     * @return
     */
    public List<FlowLog> queryFlowLogs() {
        return flowLogMapper.select(new FlowLog()
                .setFlow_type(this.flowData.getFlow_type())
                .setBiz_id(this.flowData.getBuz_id()));
    }

    /**
     * 获取当前流程data
     * @return
     */
    public FlowData getFlowData() {
        FlowData ret = new FlowData();
        BeanUtils.copyProperties(this.flowData, ret);
        return ret;
    }

    @Override
    public boolean isActive() {
        return  !const_end.equals(this.flowData.getStatus());
    }

    @Override
    public String getStepAction(String stepName) {
        return (String) this.getStepVar(stepName, const_action);
    }

    /**
     * 获取指定步骤变量
     * @param stepName
     * @return
     */
    public Object getStepVar(String stepName, String varName) {
        Object obj = this.getVar(const_step + stepName);
        Map<String, Object> stepVar;
        if(obj == null)
            return null;

        if(obj instanceof Map){
            stepVar = (Map<String, Object>) obj;
        }
        else if(obj instanceof String){
            stepVar = JSON.parseObject((String) obj, Map.class);
        }else{
            return null;
        }

        return stepVar.get(varName);
    }

    @Override
    public void defer(int deferTime) {
        int oldTime = this.flowData.getLimit_time();
        this.flowDataMapper.updateByPrimaryKey(this.flowData.setLimit_time(oldTime + deferTime));
    }

    @Override
    public Object getStepData(String stepName) {
        return this.getStepVar(stepName, const_data);
    }

    @Override
    public void startTime() {
        if(this.flowData.getBegin_time() != null){
            log.info("当前流程计时已启动，请勿重复操作");
            return;
        }

        this.flowData.setBegin_time(new Date());
        flowDataMapper.updateByPrimaryKey(this.flowData);
        log.info("流程计时启动成功");
    }

    @Override
    public void resetTime() {
        this.flowData.setLimit_time(this.findCurrentStep().getLimittime());
        this.flowData.setBegin_time(new Date());
        flowDataMapper.updateByPrimaryKey(this.flowData);
    }

    @Override
    public void setFlowTimeHelper(FlowTimeHelper helper) {
        this.flowTimeHelper = helper;
    }

    @Override
    public void delayFlowTime(Integer time) {
        if(this.flowData.getLimit_time() == null){
            return;
        }

        // 更新流程数据时限
        this.flowData.setLimit_time(this.flowData.getLimit_time() + time);
        flowDataMapper.updateByPrimaryKey(this.flowData);

        // 更新日志时限
        List<FlowLog> logs = this.queryFlowLogs();
        FlowLog log = logs.get(logs.size()-1);
        this.flowLogMapper.updateByPrimaryKey(log.setLimittime(this.flowData.getLimit_time()));
    }

    public String getCurrent_step() {
        return this.flowData.getCurrent_step();
    }


    /**
     * 切换到下一个步骤
     * @param flowData
     * @param currStep
     * @param nextStep
     * @param currUser
     * @param nextUser
     * @param action
     * @param comment
     */
    private void goNextStep(FlowData flowData, FlowStep currStep, FlowStep nextStep,
                            FlowUser currUser, FlowUser nextUser,
                            String action, String comment) {
        // 更新流程log
        FlowLog flowLog = this.buildFlowLog(currStep, flowData.getBuz_id(), currUser,
                comment,
                action == null ? "submit" : action);
        flowLog.setFlow_type(flowData.getFlow_type());
        flowLogMapper.insert(flowLog);

        // 更新流程数据信息
        flowData.setStatus(nextStep.getFlow_status());
        flowData.setUpdate_user(currUser.getAccount());
        flowData.setUpdate_date(new Date());
        flowData.setCurrent_user(nextUser.getAccount());
        flowData.setCurrent_role(nextStep.getStep_role());
        flowData.setCurrent_dept(nextUser.getDeptCode());
        flowData.setCurrent_step(nextStep.getStep());
        flowData.setFlow_action(action);
        if(flowTimeHelper != null){
            Integer time = flowTimeHelper.flowTime(nextStep.getStep(), flowData.getBuz_id(), nextStep.getLimittime());
            if(time > 0){
                flowData.setLimit_time(time);
                flowData.setBegin_time(new Date());
            }
        }
        flowDataMapper.updateByPrimaryKey(flowData);

        // 执行流程自定义action
        if(!Strings.isNullOrEmpty(currStep.getClass_path())){
            try {
                ((FlowAction) OkflowSpringContextHolder.getBean(currStep.getClass_path()))
                        .submitAfter(flowService.getCurrentFlow(flowData.getFlow_type(), flowData.getBuz_id())
                            , action);
            } catch (Exception e) {
                log.error("执行自定义action异常", e);
            }
        }

    }


    /**
     * 构建一个Flowlog对象
     * @param step
     * @param bizId
     * @return
     */
    private FlowLog buildFlowLog(FlowStep step, String bizId, FlowUser currUser, String comments, String flowAction) {
        FlowLog log = new FlowLog();
        log.setStep(step == null ? "-1" : step.getStep());
        log.setBiz_id(bizId);
        log.setComments(comments);
        log.setFlow_action(flowAction);
        log.setUpdate_dept(currUser.getDeptCode());
        log.setUpdate_role(currUser.getRoleCode());
        log.setUpdate_user(currUser.getAccount());
        log.setUpdate_date(new Date());
        log.setLimittime(step == null ? null : step.getLimittime());

        return log;
    }
}
