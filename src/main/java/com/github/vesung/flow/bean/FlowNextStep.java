package com.github.vesung.flow.bean;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/11/6
 */
public class FlowNextStep {
    private String action;
    private String next;

    public String getAction() {
        return action;
    }

    public FlowNextStep setAction(String action) {
        this.action = action;
        return this;
    }

    public String getNext() {
        return next;
    }

    public FlowNextStep setNext(String next) {
        this.next = next;
        return this;
    }
}
