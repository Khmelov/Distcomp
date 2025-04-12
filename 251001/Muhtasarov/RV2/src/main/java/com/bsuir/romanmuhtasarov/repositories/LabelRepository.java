package com.bsuir.romanmuhtasarov.repositories;

import com.bsuir.romanmuhtasarov.domain.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
}
