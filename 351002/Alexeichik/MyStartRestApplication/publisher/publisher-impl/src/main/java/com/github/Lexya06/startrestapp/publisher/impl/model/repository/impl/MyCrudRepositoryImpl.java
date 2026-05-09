package com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl;


import com.github.Lexya06.startrestapp.publisher.impl.model.entity.abstraction.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Set;

@NoRepositoryBean
public interface MyCrudRepositoryImpl<T extends BaseEntity> extends JpaRepository<T, Long>, QuerydslPredicateExecutor<T> {
    @Query("SELECT e.id FROM #{#entityName} e WHERE e.id in :ids")
    Set<Long> findExistingIds(@Param("ids") Set<Long> ids);

}
