package com.bsuir.romanmuhtasarov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bsuir.romanmuhtasarov.domain.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
