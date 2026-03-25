package com.github.Lexya06.startrestapp.DataTestBuilder;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArticleTestBuilder extends DataTestBuilder {
    @Autowired
    UserTestBuilder userTestBuilder;
    @Override
    public String getUpdateBody() {
        return """
            {
                "userId": "1",
                "title": "hip hop",
                "content": "hello world"
            }
            """;
    }

    @Override
    public String getCreateBody() {
        return """
            {
                "userId": "1",
                "title": "hello",
                "content": "hello world"
            }
            """;
    }

    @Override
    public String getInvalidBody() {
        return """
            {
                "userId": "1d",
                "title": "hello",
                "content": "hello world"
            }
            """;
    }

    @Override
    public String getEntitiesPath() {
        return "/articles";
    }

    @Override
    public Integer getCreatedEntityId(){
        userTestBuilder.getCreatedEntityId();
        return super.getCreatedEntityId();
    }
}
