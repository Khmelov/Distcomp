package com.github.Lexya06.startrestapp.DataTestBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoticeTestBuilder extends DataTestBuilder {
    @Autowired
    ArticleTestBuilder articleTestBuilder;

    @Override
    public String getUpdateBody() {
        return """
            {
                "articleId": "1",
                "content": "hey girl"
            }
            """;
    }

    @Override
    public String getCreateBody() {
        return """
            {
                "articleId": "1",
                "content": "hey la la lei"
            }
            """;
    }

    @Override
    public String getInvalidBody() {
        return """
            {
                "articleId": "1f",
                "content": "hey la la lei"
            }
            """;
    }

    @Override
    public String getEntitiesPath() {
        return "/notices";
    }

    @Override
    public Integer getCreatedEntityId(){
        articleTestBuilder.getCreatedEntityId();
        return super.getCreatedEntityId();
    }
}
