package org.example.task310rest.repository;

import org.example.task310rest.model.Label;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LabelRepository extends InMemoryCrudRepository<Label> {
    public LabelRepository() {
        super(Label::getId, Label::setId);
    }

    public Optional<Label> findByName(String name) {
        return findAll().stream()
                .filter(label -> name.equals(label.getName()))
                .findFirst();
    }
}
