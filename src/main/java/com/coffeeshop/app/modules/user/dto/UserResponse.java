package com.coffeeshop.app.modules.user.dto;

import com.coffeeshop.app.modules.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private Role role;
    private boolean status;
    private LocalDateTime createdAt;
}
