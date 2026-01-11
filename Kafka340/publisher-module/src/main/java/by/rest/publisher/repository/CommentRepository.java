package by.rest.publisher.repository;

import by.rest.publisher.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    
    List<Comment> findByStoryId(Long storyId);
    
    List<Comment> findByStatus(String status);
    
    Optional<Comment> findByIdAndStoryId(UUID id, Long storyId);
    
    void deleteByIdAndStoryId(UUID id, Long storyId);
    
    boolean existsByIdAndStoryId(UUID id, Long storyId);
    
    long countByStoryId(Long storyId);
    
    long countByStatus(String status);
}   