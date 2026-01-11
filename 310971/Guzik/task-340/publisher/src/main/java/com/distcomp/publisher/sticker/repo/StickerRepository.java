package com.distcomp.publisher.sticker.repo;

import com.distcomp.publisher.sticker.domain.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
}
