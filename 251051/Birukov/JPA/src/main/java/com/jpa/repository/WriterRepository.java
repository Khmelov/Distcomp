package com.jpa.repository;

import com.jpa.entity.Writer;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface  WriterRepository extends JpaRepository<Writer, Long> {
	
	boolean existsById(Long id);
	Optional<Writer> findById(Long id);
	List<Writer> findAll();
	
	boolean existsByLogin(String login);
	Optional<Writer> findByLogin(String login);
}