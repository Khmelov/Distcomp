package com.aitor.publisher.repository;

import com.aitor.publisher.model.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    List<Sticker> findByName(String name);
}
