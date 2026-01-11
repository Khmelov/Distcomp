package by.rest.discussion.mapper;

// Простая версия без MapStruct
import by.rest.discussion.domain.Comment;
import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    
    public Comment toEntity(CommentRequestTo dto) {
        if (dto == null) {
            return null;
        }
        
        Comment comment = new Comment();
        comment.setStoryId(dto.getStoryId());
        comment.setContent(dto.getContent());
        return comment;
    }
    
    public CommentResponseTo toResponse(Comment entity) {
        if (entity == null) {
            return null;
        }
        
        CommentResponseTo response = new CommentResponseTo();
        response.setId(entity.getId());
        response.setStoryId(entity.getStoryId());
        response.setContent(entity.getContent());
        return response;
    }
}