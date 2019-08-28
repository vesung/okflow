package com.github.vesung.flow.service;

import com.github.vesung.flow.persistence.model.FlowDef;

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
    FlowDef findNextStep(Flow flow, String action);
}
