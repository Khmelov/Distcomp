package com.distcomp.publisher.sticker.repo;

import com.distcomp.publisher.sticker.domain.Sticker;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    Optional<Sticker> findByName(String name);

    List<Sticker> findAllByNameIn(Collection<String> names);

    @Modifying
    @Query("delete from Sticker s where s.name in :names")
    void deleteByNameIn(@Param("names") Collection<String> names);
}
