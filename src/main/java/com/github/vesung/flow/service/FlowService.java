package com.github.vesung.flow.service;

import com.github.vesung.flow.IFlowUser;

/**
 * 流程管理Service
 *
 * @author fengshuonan
 * @Date 2019-05-11 17:06:29
 */
public interface FlowService {

    /**
     * 启动新流程,初始状态为”草稿“，
     * @param flowType 流程类型
     * @param buzId    业务数据id
     * @param optionUser     当前操作用户
     * @param flowDept 流程所属机构
     * @return
     */
    public Flow start(String flowType, String buzId, IFlowUser optionUser, String flowDept);

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
    int countWaitingfor(String userAccount);

}
