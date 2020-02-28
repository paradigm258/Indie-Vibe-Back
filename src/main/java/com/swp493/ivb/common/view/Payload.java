package com.swp493.ivb.common.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Payload<T> {

    @JsonInclude(Include.NON_EMPTY)
    private String status;

    @JsonInclude(Include.NON_EMPTY)
    private T data;

    @JsonInclude(Include.NON_EMPTY)
    private String message;

    @JsonInclude(Include.NON_EMPTY)
    private Integer code;

    public Payload() {
    }

    public Payload<T> success(T data) {
        this.status = "success";
        this.data = data;
        return this;
    }

    public Payload<T> fail(T data) {
        this.status = "fail";
        this.data = data;
        return this;
    }

    public Payload<T> error(String message) {
        this.status = "fail";
        this.message = message;
        return this;
    }

    public Payload<T> error(String message, T data) {
        this.error(message);
        this.data = data;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
