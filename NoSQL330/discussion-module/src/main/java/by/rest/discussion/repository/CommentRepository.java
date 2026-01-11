package by.rest.discussion.repository;

import by.rest.discussion.domain.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, Comment.CommentKey> {
    
    // Этот метод будет работать автоматически благодаря именованию
    // Spring Data сгенерирует запрос: SELECT * FROM tbl_comment WHERE story_id = ?0
    List<Comment> findByKeyStoryId(Long storyId);
    
    // Альтернативно с @Query
    @Query("SELECT * FROM tbl_comment WHERE story_id = ?0")
    List<Comment> findByStoryId(Long storyId);
    
    // С пагинацией
    @Query("SELECT * FROM tbl_comment WHERE story_id = ?0")
    Slice<Comment> findByStoryId(Long storyId, Pageable pageable);
    
    // Поиск по составному ключу
    Optional<Comment> findByKeyStoryIdAndKeyId(Long storyId, Long id);
    
    // Альтернативно с @Query
    @Query("SELECT * FROM tbl_comment WHERE story_id = ?0 AND id = ?1")
    Optional<Comment> findByStoryIdAndId(Long storyId, Long id);
    
    @Query("DELETE FROM tbl_comment WHERE story_id = ?0 AND id = ?1")
    void deleteByStoryIdAndId(Long storyId, Long id);
    
    // Удаление через ключ
    void deleteByKeyStoryIdAndKeyId(Long storyId, Long id);
    
    @Query("SELECT MAX(id) FROM tbl_comment WHERE story_id = ?0 ALLOW FILTERING")
    Long findMaxIdByStoryId(Long storyId);
    
    // Count через @Query (более эффективно)
    @Query("SELECT COUNT(*) FROM tbl_comment WHERE story_id = ?0 ALLOW FILTERING")
    long countByStoryId(Long storyId);
    
    // Простой count через список (менее эффективно, но работает)
    default long countByStoryIdSimple(Long storyId) {
        return findByStoryId(storyId).size();
    }
}