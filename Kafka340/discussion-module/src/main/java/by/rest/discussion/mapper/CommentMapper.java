package by.rest.discussion.mapper;

import by.rest.discussion.domain.Comment;
import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    
    public Comment toEntity(CommentRequestTo dto, Long id) {
        if (dto == null) {
            return null;
        }
        
        // Создаем Comment с помощью конструктора
        return new Comment(dto.getStoryId(), id, dto.getContent());
    }
    
    public Comment toEntity(CommentRequestTo dto) {
        // Без ID - ID будет сгенерирован в сервисе
        return new Comment(dto.getStoryId(), null, dto.getContent());
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