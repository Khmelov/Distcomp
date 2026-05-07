package com.github.Lexya06.startrestapp.controller.realization;
import com.github.Lexya06.startrestapp.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.model.dto.request.ArticleRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.ArticleResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.realization.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${server.api.base-path.v1}/articles")
@Validated
public class ArticleController extends BaseController<Article, ArticleRequestTo, ArticleResponseTo> {
    ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
    @Override
    protected BaseEntityService<Article, ArticleRequestTo, ArticleResponseTo> getBaseService() {
        return articleService;
    }


}
