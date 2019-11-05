package com.github.vesung.flow.service;

import com.github.vesung.flow.persistence.model.FlowStep;

/**
 *
 * @author wangjing.dc@qq.com
 * @since 2019/6/17
 */
public interface NextStepFinder {
    /**
     * 查找下一步定义
     * @param flow
     * @param action
     * @return
     */
    FlowStep findNextStep(Flow flow, String action);
}
