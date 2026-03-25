package com.github.Lexya06.startrestapp;

import com.github.Lexya06.startrestapp.DataTestBuilder.DataTestBuilder;
import com.github.Lexya06.startrestapp.DataTestBuilder.NoticeTestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoticeRestTest extends FunctionalTest {
    @Autowired
    NoticeTestBuilder noticeTestBuilder;

    @Override
    protected DataTestBuilder getDataTestBuilder() {
        return noticeTestBuilder;
    }
}
