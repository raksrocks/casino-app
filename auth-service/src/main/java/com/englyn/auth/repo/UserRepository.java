package com.englyn.auth.repo;

import com.englyn.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Custom method to find a user by username
    UserEntity findByUsername(String username);

    UserEntity findByToken(String token);
}

