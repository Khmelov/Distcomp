package com.github.Lexya06.startrestapp.DataTestBuilder;

import org.springframework.stereotype.Component;

@Component
public class LabelTestBuilder extends DataTestBuilder {
    @Override
    public String getUpdateBody() {
        return """
            {
                "name": "myTag"
            }
            """;
    }

    @Override
    public String getCreateBody() {
        return """
            {
                "name": "lexyaTag"
            }
            """;
    }

    @Override
    public String getInvalidBody() {
        return """
            {
                "name": "l"
            }
            """;
    }
    @Override
    public String getEntitiesPath() {
        return "/labels";
    }
}
