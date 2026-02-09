package com.example.lab.repository;

import java.util.List;

import com.example.lab.model.User;

public interface UserRepository extends CrudRepository<User> {
    @Override
    List<User> getAllEntities();
}
