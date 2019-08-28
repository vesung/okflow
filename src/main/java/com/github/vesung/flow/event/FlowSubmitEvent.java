package com.github.vesung.flow.event;

import com.github.vesung.flow.persistence.model.FlowData;
import org.springframework.context.ApplicationEvent;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/7/29
 */
public class FlowSubmitEvent extends ApplicationEvent {
    private final FlowData flowData;

    public FlowSubmitEvent(Object source, FlowData flowData) {
        super(source);
        this.flowData =flowData;
    }

    public FlowData getFlowData() {
        return flowData;
    }
}
