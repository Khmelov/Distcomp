package com.github.Lexya06.startrestapp;

import com.github.Lexya06.startrestapp.DataTestBuilder.ArticleTestBuilder;
import com.github.Lexya06.startrestapp.DataTestBuilder.DataTestBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ArticleRestTest extends FunctionalTest{
    @Autowired
    ArticleTestBuilder articleTestBuilder;


    @Override
    protected DataTestBuilder getDataTestBuilder() {
        return articleTestBuilder;
    }
}