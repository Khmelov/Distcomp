package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.User;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository extends MyCrudRepository<User> {
    HashMap<Long, User> userMap = new HashMap<>();
    @Override
    protected Map<Long, User> getBaseEntityMap() {
        return userMap;
    }
}
