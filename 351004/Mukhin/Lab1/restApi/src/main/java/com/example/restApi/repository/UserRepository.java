package com.example.restApi.repository;

import com.example.restApi.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends InMemoryRepository<User> {
}
