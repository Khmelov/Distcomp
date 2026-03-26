package com.example.lab.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab.publisher.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
