package com.github.vesung.flow.persistence.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/8/29
 */
public class Base implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    protected Integer id;

    /**更新时间*/
    private Date update_date;
    /**更新用户用户*/
    private String update_user;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public Base setUpdate_date(Date update_date) {
        this.update_date = update_date;
        return this;
    }

    public String getUpdate_user() {
        return update_user;
    }

    public Base setUpdate_user(String update_user) {
        this.update_user = update_user;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public Base setId(Integer id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "Base{" +
                "id=" + id +
                '}';
    }
}
