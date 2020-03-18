package com.swp493.ivb.common.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Payload<T> {

    @JsonInclude(Include.NON_EMPTY)
    private String status;

    @JsonInclude(Include.NON_NULL)
    private T data;

    @JsonInclude(Include.NON_NULL)
    private String message;

    @JsonInclude(Include.NON_NULL)
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

    public static ResponseEntity<?> internalError(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Payload<>().fail("Something is wrong"));
    }

    public static  ResponseEntity<Payload<Object>> successResponse(Object data){
        return ResponseEntity.ok().body(new Payload<>().success(data));
    }

    public static  ResponseEntity<Payload<Object>> failureResponse(Object message){
        return ResponseEntity.badRequest().body(new Payload<>().fail(message));
    }
}
