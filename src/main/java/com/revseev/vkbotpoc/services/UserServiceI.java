package com.revseev.vkbotpoc.services;

import com.revseev.vkbotpoc.model.User;

import java.util.List;

public interface UserServiceI {

    void add(User user);

    void deleteById(Long userId);

    void deleteAll();

    User getById(Long userId);

    List<User> getAll();
}