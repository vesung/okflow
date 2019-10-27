package com.github.vesung.flow.bean;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/7/23
 */
public class Result<T> {
    @ApiModelProperty(value = "状态码，200：执行成功，99：业务逻辑异常，98：流程逻辑异常", example = "200")
    private int code;
    @ApiModelProperty(value = "数据体")
    private T data;
    @ApiModelProperty(value = "返回信息描述")
    private String message;

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}
