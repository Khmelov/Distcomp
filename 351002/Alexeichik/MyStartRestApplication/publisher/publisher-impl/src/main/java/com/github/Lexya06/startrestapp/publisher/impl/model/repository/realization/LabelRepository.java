package com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends MyCrudRepositoryImpl<Label> {
    Label findByName(String name);
    boolean existsByName(String name);
}
