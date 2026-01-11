package by.rest.spec;

import by.rest.domain.Story;
import org.springframework.data.jpa.domain.Specification;

public class StorySpecs {
    public static Specification<Story> hasEditorLogin(String login) {
        return (root, query, cb) -> (login == null || login.isBlank()) ? null :
                cb.equal(root.join("editor").get("login"), login);
    }
    
    public static Specification<Story> hasTagId(Long tagId) {
        return (root, query, cb) -> (tagId == null) ? null :
                cb.equal(root.join("tags").get("id"), tagId);
    }
    
    public static Specification<Story> titleContains(String title) {
        return (root, query, cb) -> (title == null || title.isBlank()) ? null :
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
    
    public static Specification<Story> contentContains(String content) {
        return (root, query, cb) -> (content == null || content.isBlank()) ? null :
                cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }
}