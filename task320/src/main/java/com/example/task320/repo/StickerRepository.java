package com.example.task320.repo;

import com.example.task320.domain.StickerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<StickerEntity, Long> {
    boolean existsByName(String name);
}
