package com.techvalley.monitor.dto;

import com.techvalley.monitor.domain.enums.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private MemberRole role;
}
