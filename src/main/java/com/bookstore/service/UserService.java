package com.bookstore.service;

import com.bookstore.domain.User;
import java.util.Optional;

public interface UserService {
    Optional<User> getCurrentUser();
    User getUserById(Long id);
    User updateUser(User user);
    void deleteUser(Long id);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}