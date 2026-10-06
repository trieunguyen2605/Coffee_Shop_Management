package com.coffeeshop.app.modules.user.service;

import com.coffeeshop.app.modules.user.dto.ChangePasswordRequest;
import com.coffeeshop.app.modules.user.dto.CreateUserRequest;
import com.coffeeshop.app.modules.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    Page<UserResponse> getUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse updateUserStatus(Long id, boolean status);

    void changePassword(Long userId, ChangePasswordRequest request);
}
