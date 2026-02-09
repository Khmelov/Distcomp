package com.example.lab1.repository;

import java.util.List;

import com.example.lab1.model.User;

public interface UserRepository extends CrudRepository<User> {
    @Override
    List<User> getAllEntities();
}
