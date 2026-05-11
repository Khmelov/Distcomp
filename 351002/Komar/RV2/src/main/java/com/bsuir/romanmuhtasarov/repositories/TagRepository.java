package com.bsuir.romanmuhtasarov.repositories;

import com.bsuir.romanmuhtasarov.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
