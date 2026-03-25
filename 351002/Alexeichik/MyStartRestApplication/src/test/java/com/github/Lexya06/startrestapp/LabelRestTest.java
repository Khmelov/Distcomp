package com.github.Lexya06.startrestapp;

import com.github.Lexya06.startrestapp.DataTestBuilder.DataTestBuilder;
import com.github.Lexya06.startrestapp.DataTestBuilder.LabelTestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LabelRestTest extends FunctionalTest {
    @Autowired
    LabelTestBuilder labelTestBuilder;


    @Override
    protected DataTestBuilder getDataTestBuilder() {
        return labelTestBuilder;
    }
}
