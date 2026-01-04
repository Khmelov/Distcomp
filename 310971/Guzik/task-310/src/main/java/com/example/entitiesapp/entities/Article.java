package com.example.entitiesapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Article extends BaseEntity {
    private Long writerId;
    private String title;
    private String content;
    private List<Post> posts = new ArrayList<>();
    private Set<Sticker> stickers = new HashSet<>();
}