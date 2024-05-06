package by.bsuir.dc.lab2.services.repos;

import by.bsuir.dc.lab2.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
