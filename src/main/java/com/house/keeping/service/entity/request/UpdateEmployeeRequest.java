package com.house.keeping.service.entity.request;

import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private String nickname;
    private String email;
    private String phone;
    private String idCard;
    private String address;
    private String workYears;
    private String identity;
    private Boolean isTeamLeader;
    private String teamSize;
    private String resume;
}
