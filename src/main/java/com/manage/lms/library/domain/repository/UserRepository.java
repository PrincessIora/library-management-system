package com.manage.lms.library.domain.repository;

import com.manage.lms.library.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
