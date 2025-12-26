package com.restApp.model;

import java.util.ArrayList;
import java.util.List;

public class Mark extends BaseEntity {
    private String name;

    private List<News> news = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
