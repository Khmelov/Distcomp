package org.example.task310rest.repository;

import org.example.task310rest.model.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface JpaCrudRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {
    
    Optional<T> findById(ID id);
    
    List<T> findAll();
    
    List<T> findAll(Sort sort);
    
    Page<T> findAll(Pageable pageable);
    
    <S extends T> S save(S entity);
    
    void deleteById(ID id);
    
    boolean existsById(ID id);
}

