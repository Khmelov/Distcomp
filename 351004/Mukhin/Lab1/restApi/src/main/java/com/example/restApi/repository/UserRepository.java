package com.example.restApi.repository;

import com.example.restApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository   extends JpaRepository<User,Long> {
}
