package com.example.rv.impl.issue;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Issue {
    Long id;
    Long creatorId;
    String title;
    String content;
    String created;
    String modified;
}
