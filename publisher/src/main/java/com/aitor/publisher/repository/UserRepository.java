package com.aitor.publisher.repository;

import com.aitor.publisher.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
