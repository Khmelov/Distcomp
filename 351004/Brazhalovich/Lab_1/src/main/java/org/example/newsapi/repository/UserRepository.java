package org.example.newsapi.repository;

import org.example.newsapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository уже содержит методы findAll, findById, save, deleteById
    // Дополнительные методы можно объявлять здесь (например, findByLogin), если понадобятся
    boolean existsByLogin(String login);
}