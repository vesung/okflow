package com.github.vesung.flow.service;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/6/15
 */
public interface FlowAction {

    /**
     * 执行流程自定义action
     * @param flow 流程数据
     * @param currentAction
     */
    void submitAfter(Flow flow, String currentAction);
}
