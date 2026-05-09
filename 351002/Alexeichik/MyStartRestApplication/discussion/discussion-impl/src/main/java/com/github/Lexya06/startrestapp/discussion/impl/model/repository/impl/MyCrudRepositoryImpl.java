package com.github.Lexya06.startrestapp.discussion.impl.model.repository.impl;


import com.github.Lexya06.startrestapp.discussion.impl.model.entity.abstraction.BaseEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface MyCrudRepositoryImpl<T extends BaseEntity<K>, K> extends CassandraRepository<T, K>{

}
