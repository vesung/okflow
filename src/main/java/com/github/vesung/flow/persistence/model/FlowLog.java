package com.github.vesung.flow.persistence.model;

import javax.persistence.Table;

@Table(name = "flow_log")
public class FlowLog extends Base {

    // 当前步骤
    private String step;
    // 业务表关联id
    private String biz_id;
    // 审批意见
    private String comments;
    // 流程时限
    private Integer limittime;
    // 审批动作
    private String flow_action;
    // 最后更新角色
    private String update_role;
    // 最后更新部门
    private String update_dept;
    // 流程类型
    private String flow_type;

    public String getFlow_type() {
        return flow_type;
    }

    public FlowLog setFlow_type(String flow_type) {
        this.flow_type = flow_type;
        return this;
    }

    public String getStep() {
        return step;
    }

    public FlowLog setStep(String step) {
        this.step = step;
        return this;
    }

    public String getBiz_id() {
        return biz_id;
    }

    public FlowLog setBiz_id(String biz_id) {
        this.biz_id = biz_id;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public FlowLog setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Integer getLimittime() {
        return limittime;
    }

    public FlowLog setLimittime(Integer limittime) {
        this.limittime = limittime;
        return this;
    }

    public String getFlow_action() {
        return flow_action;
    }

    public FlowLog setFlow_action(String flow_action) {
        this.flow_action = flow_action;
        return this;
    }

    public String getUpdate_role() {
        return update_role;
    }

    public FlowLog setUpdate_role(String update_role) {
        this.update_role = update_role;
        return this;
    }

    public String getUpdate_dept() {
        return update_dept;
    }

    public FlowLog setUpdate_dept(String update_dept) {
        this.update_dept = update_dept;
        return this;
    }
}
