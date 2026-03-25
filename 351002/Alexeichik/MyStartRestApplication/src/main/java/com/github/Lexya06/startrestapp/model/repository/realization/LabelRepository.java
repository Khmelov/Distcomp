package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends MyCrudRepositoryImpl<Label> {
    Label findByName(String name);
    boolean existsByName(String name);
}
