package by.molchan.Publisher.utils;


import by.molchan.Publisher.DTOs.Requests.ArticleRequestDTO;
import by.molchan.Publisher.DTOs.Requests.CommentRequestDTO;
import by.molchan.Publisher.services.ArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CommentValidator implements Validator {
    private final ArticlesService articlesService;

    @Autowired
    public CommentValidator(ArticlesService articlesService) {
        this.articlesService = articlesService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ArticleRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            CommentRequestDTO comment = (CommentRequestDTO) target;
            if (!articlesService.existsById(comment.getArticleId())){
                errors.rejectValue("articleId", null, "Such articleId doesn't exist");
            }
        }
    }
}
