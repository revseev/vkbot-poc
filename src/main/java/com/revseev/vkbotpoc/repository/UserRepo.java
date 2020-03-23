package com.revseev.vkbotpoc.repository;

import com.revseev.vkbotpoc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query(value = "SELECT u FROM User u WHERE u.vkId = ?1"/*, nativeQuery=true*/)
    User findByVkId(Integer vkId);
}
