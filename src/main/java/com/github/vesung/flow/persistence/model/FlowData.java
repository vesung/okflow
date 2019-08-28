package com.github.vesung.flow.persistence.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "flow_data")
public class FlowData {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    // 流程类型
    private String flow_type;
    // 业务id
    private String buz_id;
    // 流程状态
    private String status;
    // 更新人
    private String update_user;
    // 更新时间
    private Date update_date;
    // 当前处理人account
    @Column(name = "current_user_x")
    private String current_user;
    // 当前处理角色id
    private String current_role;
    // 当前处理机构id
    private String current_dept;
    // 流程启动时间
    private Date start_date;
    // 流程结束时间
    private Date end_date;
    // 当前步骤
    private String current_step;
    // 流程所属步骤
    private String flow_dept;
    // 流程时限
    private Integer limit_time;
    // 当前步骤开始时间
    private Date begin_time;
    private String creator;
    private String vars;

    public Integer getId() {
        return id;
    }

    public FlowData setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getFlow_type() {
        return flow_type;
    }

    public FlowData setFlow_type(String flow_type) {
        this.flow_type = flow_type;
        return this;
    }

    public String getBuz_id() {
        return buz_id;
    }

    public FlowData setBuz_id(String buz_id) {
        this.buz_id = buz_id;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public FlowData setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getUpdate_user() {
        return update_user;
    }

    public FlowData setUpdate_user(String update_user) {
        this.update_user = update_user;
        return this;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public FlowData setUpdate_date(Date update_date) {
        this.update_date = update_date;
        return this;
    }

    public String getCurrent_user() {
        return current_user;
    }

    public FlowData setCurrent_user(String current_user) {
        this.current_user = current_user;
        return this;
    }

    public String getCurrent_role() {
        return current_role;
    }

    public FlowData setCurrent_role(String current_role) {
        this.current_role = current_role;
        return this;
    }

    public String getCurrent_dept() {
        return current_dept;
    }

    public FlowData setCurrent_dept(String current_dept) {
        this.current_dept = current_dept;
        return this;
    }

    public Date getStart_date() {
        return start_date;
    }

    public FlowData setStart_date(Date start_date) {
        this.start_date = start_date;
        return this;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public FlowData setEnd_date(Date end_date) {
        this.end_date = end_date;
        return this;
    }

    public String getCurrent_step() {
        return current_step;
    }

    public FlowData setCurrent_step(String current_step) {
        this.current_step = current_step;
        return this;
    }

    public String getFlow_dept() {
        return flow_dept;
    }

    public FlowData setFlow_dept(String flow_dept) {
        this.flow_dept = flow_dept;
        return this;
    }

    public Integer getLimit_time() {
        return limit_time;
    }

    public FlowData setLimit_time(Integer limit_time) {
        this.limit_time = limit_time;
        return this;
    }

    public Date getBegin_time() {
        return begin_time;
    }

    public FlowData setBegin_time(Date begin_time) {
        this.begin_time = begin_time;
        return this;
    }

    public FlowData setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public String getVars() {
        return vars;
    }

    public FlowData setVars(String vars) {
        this.vars = vars;
        return this;
    }
}
