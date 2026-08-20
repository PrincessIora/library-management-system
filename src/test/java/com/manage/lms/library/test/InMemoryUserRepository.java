package com.manage.lms.library.test;

import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();

    private int nextId = 1;

    @Override
    public User save(User user) {

        if (user.getId() == 0) {
            user.setId(nextId++);
            users.add(user);
        }

        return user;
    }

    @Override
    public Optional<User> findById(int id) {

        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return users.stream()
                .filter(user ->
                        user.getUsername().equals(username)
                )
                .findFirst();
    }

    @Override
    public List<User> findAll() {

        return new ArrayList<>(users);
    }
}