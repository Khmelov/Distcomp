package org.example.task310rest.repository;

import org.example.task310rest.model.Label;
import org.springframework.stereotype.Repository;

@Repository
public class LabelRepository extends InMemoryCrudRepository<Label> {
    public LabelRepository() {
        super(Label::getId, Label::setId);
    }
}


