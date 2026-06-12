package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
