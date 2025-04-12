package by.molchan.Publisher.utils;


import by.molchan.Publisher.DTOs.Requests.ArticleRequestDTO;
import by.molchan.Publisher.services.ArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ArticleValidator implements Validator {
    private final ArticlesService articlesService;

    @Autowired
    public ArticleValidator(ArticlesService articlesService) {
        this.articlesService = articlesService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ArticleRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            ArticleRequestDTO article = (ArticleRequestDTO) target;
            if (articlesService.existsByTitle(article.getTitle())){
                errors.rejectValue("title", null, "Article with such title already exists");
            }
        }
    }
}
