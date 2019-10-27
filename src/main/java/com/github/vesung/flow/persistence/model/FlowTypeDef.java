package com.github.vesung.flow.persistence.model;

import javax.persistence.Table;
import java.util.Date;

/**
 * 流程种类定义
 * @author wangjing.dc@qq.com
 * @since 2019/10/27
 */
@Table(name = "flow_type_def")
public class FlowTypeDef{

    private String type;
    private String name;
    private String status;
    /**更新时间*/
    private Date update_date;
    /**更新用户用户*/
    private String update_user;

    public Date getUpdate_date() {
        return update_date;
    }

    public FlowTypeDef setUpdate_date(Date update_date) {
        this.update_date = update_date;
        return this;
    }

    public String getUpdate_user() {
        return update_user;
    }

    public FlowTypeDef setUpdate_user(String update_user) {
        this.update_user = update_user;
        return this;
    }

    public String getType() {
        return type;
    }

    public FlowTypeDef setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public FlowTypeDef setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public FlowTypeDef setStatus(String status) {
        this.status = status;
        return this;
    }
}
