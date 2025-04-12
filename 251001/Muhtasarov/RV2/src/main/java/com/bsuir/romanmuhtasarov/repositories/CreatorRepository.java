package com.bsuir.romanmuhtasarov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bsuir.romanmuhtasarov.domain.entity.Creator;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
}
