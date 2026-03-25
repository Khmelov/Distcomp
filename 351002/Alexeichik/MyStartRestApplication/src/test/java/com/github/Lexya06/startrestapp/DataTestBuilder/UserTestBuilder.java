package com.github.Lexya06.startrestapp.DataTestBuilder;

import org.springframework.stereotype.Component;

@Component
public class UserTestBuilder extends DataTestBuilder {

    @Override
    public String getUpdateBody() {
        return """
            {
                "login": "adminn",
                "password": "adminnio",
                "firstname": "Yana",
                "lastname": "Lexya"
            }
            """;
    }

    @Override
    public String getCreateBody() {
        return """
            {
                "login": "helloo",
                "password": "adminnio",
                "firstname": "Yana",
                "lastname": "Lexya"
            }
            """;
    }

    @Override
    public String getInvalidBody() {
        return """
            {
                "login": "adminn",
                "password": "adminnio",
                "firstname": "Y",
                "lastname": "L"
            }
            """;
    }

    @Override
    public String getEntitiesPath() {
        return "/users";
    }
}
