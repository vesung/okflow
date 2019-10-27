package com.github.vesung.flow.service;

import com.github.vesung.flow.FlowUser;
import com.github.vesung.flow.persistence.model.FlowData;
import com.github.vesung.flow.persistence.model.FlowDef;
import com.github.vesung.flow.persistence.model.FlowLog;

import java.util.List;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/6/19
 */
public interface Flow {

    /**
     * 当前流程是否
     * @return
     */
    boolean isTransfering();

    /**
     * 获取当前流程变量
     * @param action
     * @return
     */
    Object getVar(String action);

    /**
     * 查找指定流程定义
     * @param stepName
     * @return
     */
    FlowDef findStepByName(String stepName);

    /**
     * 设置流程附加数据
     * @param varName
     * @param varValue
     * @return
     */
    Flow setVar(String varName, Object varValue);

    /**
     * 切换流程机构
     * @param destDept
     */
    Flow transferDept(String destDept);

    /**
     * 查找当前流程下一步处理人列表
     * @param action
     * @return
     */
    List<FlowUser> findNextUsers(String action);

    /**
     * 提交当前流程
     * @param action
     * @param currUser
     * @param nextUser
     * @param comments
     * @param data 附加数据
     * @return
     */
    Flow submitTask(String action, FlowUser currUser, FlowUser nextUser, String comments, String data);

    /**
     * 获取当前步骤name
     * @return
     */
    String getCurrent_step();

    /**
     * 获取当前流程日志列表
     * @return
     */
    List<FlowLog> queryFlowLogs();

    /**
     * 获取当前流程data
     * @return
     */
    FlowData getFlowData();

    /**
     * 当前流程是否处于活动状态
     * @return
     */
    boolean isActive();

    /**
     * 获取指定步骤操作结果
     * @param stepName
     * @return
     */
    String getStepAction(String stepName);

    /**
     * 获取指定步骤变量
     * @param stepName
     * @return
     */
    Object getStepVar(String stepName, String varName);

    /**
     * 流程延期
     * @param deferTime
     */
    void defer(int deferTime);

    /**
     * 获取流程附带数据
     * @param stepName
     * @return
     */
    Object getStepData(String stepName);

    /**
     * 启动流程计时器
     */
    void startTime();

    /**
     * 重置当前流程计时为当前时间
     */
    void resetTime();
}
