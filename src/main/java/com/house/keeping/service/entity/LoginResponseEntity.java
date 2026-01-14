package com.house.keeping.service.entity;

public class LoginResponseEntity {
    private int code;
    private String message;
    private LoginResultEntity data;

    public LoginResponseEntity(int code, String message, LoginResultEntity data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters 和 Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginResultEntity getData() {
        return data;
    }

    public void setData(LoginResultEntity data) {
        this.data = data;
    }
}
