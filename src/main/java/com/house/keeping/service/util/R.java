package com.house.keeping.service.util;


import org.springframework.util.ObjectUtils;

public class R<T> {
    private String status; // 状态码
    private String message; // 消息
    private T data; // 泛型字段，用于存储额外的数据

    // 无参构造方法（可选）
    public R() {
    }

    // 有参构造方法
    public R(T data) {
        if(ObjectUtils.isEmpty(data)){
            this.status = "500";
            this.message = "操作失败";
            this.data = data;
        }else {
            this.status = "200";
            this.message = "操作成功";
            this.data = data;
        }
    }

    public R(boolean status) {
        if (status){
            this.status = "200";
            this.message = "操作成功";
        }else {
            this.status = "500";
            this.message = "操作失败";
        }
    }

    // Getter 和 Setter 方法
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // toString 方法（可选）
    @Override
    public String toString() {
        return "R{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static R success(Object data) {
        R result = new R();
        result.setStatus("200");
        result.setMessage("成功");
        result.setData(data);
        return result;
    }
    public static R error(String message) {
        R result = new R();
        result.setStatus("999");
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}