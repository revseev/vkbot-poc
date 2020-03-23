package com.revseev.vkbotpoc.services;

import com.revseev.vkbotpoc.model.User;
import com.revseev.vkbotpoc.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService implements UserServiceI{

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void add(User user) {
        userRepo.save(user);
    }

    @Override
    public void deleteById(Long userId) {
        userRepo.deleteById(userId);
    }

    @Override
    public void deleteAll() {
        userRepo.deleteAll();
    }

    @Override
    public User getById(Long userId) {
        return userRepo.getOne(userId);
    }

    @Override
    public List<User> getAll() {
        return userRepo.findAll();
    }
}
