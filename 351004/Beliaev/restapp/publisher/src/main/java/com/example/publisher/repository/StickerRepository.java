package com.example.publisher.repository;

import com.example.publisher.model.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    Optional<Sticker> findByName(String name);
}