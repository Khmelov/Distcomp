package org.polozkov.repository.label;

import org.polozkov.entity.label.Label;
import org.polozkov.exception.BadRequestException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByName(String name);

    default Label byId(Long id) {
        return findById(id).orElseThrow(() -> new BadRequestException("Label with id "  + id + " does not exists "));
    }

}