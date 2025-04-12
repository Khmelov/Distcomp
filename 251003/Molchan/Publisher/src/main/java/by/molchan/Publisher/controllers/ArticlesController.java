package by.molchan.Publisher.controllers;


import by.molchan.Publisher.DTOs.Requests.ArticleRequestDTO;
import by.molchan.Publisher.DTOs.Responses.ArticleResponseDTO;
import by.molchan.Publisher.services.ArticlesService;
import by.molchan.Publisher.utils.ArticleValidator;
import by.molchan.Publisher.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticlesController {
    private final ArticlesService articlesService;
    private final ArticleValidator articleValidator;

    @Autowired
    public ArticlesController(ArticlesService articlesService, ArticleValidator articleValidator) {
        this.articlesService = articlesService;
        this.articleValidator = articleValidator;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponseDTO createArticle(@RequestBody @Valid ArticleRequestDTO articleRequestDTO, BindingResult bindingResult) {
        validate(articleRequestDTO, bindingResult);
        return articlesService.save(articleRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ArticleResponseDTO> getAllArticles() {
        return articlesService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponseDTO getByArticleById(@PathVariable Long id) {
        return articlesService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable long id){
        articlesService.deleteById(id);
    }


    // Non REST version for tests compliance
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponseDTO updateArticle(@RequestBody @Valid ArticleRequestDTO articleRequestDTO, BindingResult bindingResult){
        validate(articleRequestDTO, bindingResult);
        return articlesService.update(articleRequestDTO);
    }

    private void validate(ArticleRequestDTO articleRequestDTO, BindingResult bindingResult){
        articleValidator.validate(articleRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
