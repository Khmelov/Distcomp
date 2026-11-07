package com.distcomp.repository;

import com.distcomp.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Common contract of every storage repository of the application.
 *
 * <p>Adds the generic CRUD operations of {@link JpaRepository} to the dynamic
 * filtering / sorting / pagination support of {@link JpaSpecificationExecutor},
 * so every entity gets the same query capabilities without duplicated code.</p>
 *
 * @param <T> entity type handled by this repository
 */
@NoRepositoryBean
public interface BaseRepository<T extends AbstractEntity>
        extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
