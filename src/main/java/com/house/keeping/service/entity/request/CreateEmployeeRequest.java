package com.house.keeping.service.entity.request;

import lombok.Data;

@Data
public class CreateEmployeeRequest {
    private String mode; // create or select
    private Long userId; // mode=select时必填
    private String username; // mode=create时必填
    private String password; // mode=create时必填
    private String nickname;
    private String role; // 默认employee
    private String email;
    private String phone;
    private String idCard;
    private String address;
    private String realName;
    private String workYears;
    private String identity; // domestic_worker/cleaner/nanny/confinement_nanny/caregiver/hourly_worker/other
    private Boolean isTeamLeader;
    private String teamSize;
    private String resume;
}
