package by.rest.discussion.domain;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.Objects;

@Table("tbl_comment")
public class Comment {
    
    @PrimaryKey
    private CommentKey key;
    
    @Column("content")
    private String content;
    
    // Конструкторы
    public Comment() {}
    
    public Comment(Long storyId, Long id, String content) {
        this.key = new CommentKey(storyId, id);
        this.content = content;
    }
    
    // Геттеры и сеттеры
    public CommentKey getKey() { return key; }
    public void setKey(CommentKey key) { this.key = key; }
    
    public Long getId() { return key != null ? key.getId() : null; }
    public void setId(Long id) { 
        if (key != null) key.setId(id);
        else key = new CommentKey(null, id);
    }
    
    public Long getStoryId() { return key != null ? key.getStoryId() : null; }
    public void setStoryId(Long storyId) { 
        if (key != null) key.setStoryId(storyId);
        else key = new CommentKey(storyId, null);
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(key, comment.key) && 
               Objects.equals(content, comment.content);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(key, content);
    }
    
    @PrimaryKeyClass
    public static class CommentKey {
        
        @PrimaryKeyColumn(name = "story_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
        private Long storyId;
        
        @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED, 
                         ordering = Ordering.ASCENDING, ordinal = 1)
        private Long id;
        
        // Конструкторы
        public CommentKey() {}
        
        public CommentKey(Long storyId, Long id) {
            this.storyId = storyId;
            this.id = id;
        }
        
        // Геттеры и сеттеры
        public Long getStoryId() { return storyId; }
        public void setStoryId(Long storyId) { this.storyId = storyId; }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommentKey that = (CommentKey) o;
            return Objects.equals(storyId, that.storyId) && 
                   Objects.equals(id, that.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(storyId, id);
        }
    }
}