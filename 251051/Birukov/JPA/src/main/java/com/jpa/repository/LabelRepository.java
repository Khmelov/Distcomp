package com.jpa.repository;

import com.jpa.entity.Label;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
	
	boolean existsById(Long id);
	Optional<Label> findById(Long id);
	List<Label> findAll();
	
	boolean existsByName(String name);
	Optional<Label> findByName(String name);
}