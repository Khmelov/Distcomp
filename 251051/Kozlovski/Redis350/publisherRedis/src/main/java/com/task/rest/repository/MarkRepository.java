package com.task.rest.repository;

import com.task.rest.dto.MarkResponseTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long>{
}
