package com.bsuir.romanmuhtasarov.domain.response;

import java.util.List;

public record CreatorResponseTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname,
        List<NewsResponseTo> tweetlist) {

}
