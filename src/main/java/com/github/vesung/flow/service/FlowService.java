package com.github.vesung.flow.service;

import com.github.vesung.flow.FlowUser;
import com.github.vesung.flow.bean.FlowNextStep;
import com.github.vesung.flow.persistence.model.FlowStep;
import com.github.vesung.flow.persistence.model.FlowLog;
import com.github.vesung.flow.persistence.model.FlowTypeDef;

import java.util.List;
import java.util.Map;

/**
 * 流程管理Service
 *
 * @author fengshuonan
 * @Date 2019-05-11 17:06:29
 */
public interface FlowService {

    /**
     * 查询流程定义列表
     * @return
     */
    List<FlowTypeDef> listFlowType();

    /**
     * 启动新流程,初始状态为”草稿“，
     * @param flowType 流程类型
     * @param buzId    业务数据id
     * @param optionUser     当前操作用户
     * @param flowDept 流程所属机构
     * @return
     */
    public Flow start(String flowType, String buzId, FlowUser optionUser, String flowDept);

    /**
     * 获取当前流程助手
     * @param flowType
     * @param buzId
     * @return
     */
    Flow getCurrentFlow(String flowType, String buzId);

    /**
     * 计算指定人员待办事项条数
     * @param userAccount
     * @return
     */
    int countWaitingfor(String flowType, String userAccount, String roleCode);

    /**
     * 获取指定条件的日志列表
     * @param buzIds
     * @param actions,为null标识查全部
     * @return
     */
    List<FlowLog> queryFlowLogsByFilter(String flowType, List<String> buzIds, List<String> actions);

    /**
     * 修改流程步骤
     * @param flow
     */
    void updateStep(FlowStep flow);

    /**
     * 新增流程步骤
     * @param flow
     */
    void addStep(FlowStep flow);

    /**
     * 查询步骤定义
     * @param flowId
     * @return
     */
    FlowStep queryStepById(Integer flowId);

    /**
     * 删除步骤
     * @param flowId
     */
    void deleteStep(Integer flowId);

    /**
     * 获取下一步配置信息
     * @param stepId
     * @return
     */
    List<FlowNextStep> queryNextStepDef(Integer stepId);

    /**
     * 更新下一步
     * @param stepId
     * @param nexts
     */
    void updateNextStep(Integer stepId, Map<String, String> nexts);
}
