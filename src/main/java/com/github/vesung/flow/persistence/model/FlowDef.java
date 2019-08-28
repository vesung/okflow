package com.github.vesung.flow.persistence.model;

import javax.persistence.Table;

@Table(name = "flow_def")
public class FlowDef extends Base {

    /**流程类型*/
    private String type;
    /**步骤标识*/
    private String step;
    /**下一步*/
    private String next_step;
    /**角色code*/
    private String step_role;
    /**特殊处理类*/
    private String class_path;
    /**流程状态*/
    private String flow_status;
    /**状态描述*/
    private String status_name;
    /**当前步骤时限*/
    private Integer limittime;

    public String getType() {
        return type;
    }

    public FlowDef setType(String type) {
        this.type = type;
        return this;
    }

    public String getStep() {
        return step;
    }

    public FlowDef setStep(String step) {
        this.step = step;
        return this;
    }

    public String getNext_step() {
        return next_step;
    }

    public FlowDef setNext_step(String next_step) {
        this.next_step = next_step;
        return this;
    }

    public String getStep_role() {
        return step_role;
    }

    public FlowDef setStep_role(String step_role) {
        this.step_role = step_role;
        return this;
    }

    public String getClass_path() {
        return class_path;
    }

    public FlowDef setClass_path(String class_path) {
        this.class_path = class_path;
        return this;
    }

    public String getFlow_status() {
        return flow_status;
    }

    public FlowDef setFlow_status(String flow_status) {
        this.flow_status = flow_status;
        return this;
    }

    public String getStatus_name() {
        return status_name;
    }

    public FlowDef setStatus_name(String status_name) {
        this.status_name = status_name;
        return this;
    }

    public Integer getLimittime() {
        return limittime;
    }

    public FlowDef setLimittime(Integer limittime) {
        this.limittime = limittime;
        return this;
    }
}
