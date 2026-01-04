package com.task310.socialnetwork.repository;

import com.task310.socialnetwork.model.Label;
import java.util.Optional;

public interface LabelRepository extends CrudRepository<Label, Long> {
    Optional<Label> findByName(String name);
}