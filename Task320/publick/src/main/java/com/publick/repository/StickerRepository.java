package com.publick.repository;

import com.publick.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerRepository extends CrudRepository<Sticker, Long> {
}