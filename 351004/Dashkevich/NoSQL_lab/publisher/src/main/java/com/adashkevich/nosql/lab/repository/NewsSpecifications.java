package com.adashkevich.nosql.lab.repository;

import com.adashkevich.nosql.lab.model.Marker;
import com.adashkevich.nosql.lab.model.News;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class NewsSpecifications {
    private NewsSpecifications() {}

    public static Specification<News> titleContains(String title) {
        return (root, query, cb) -> title == null || title.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<News> contentContains(String content) {
        return (root, query, cb) -> content == null || content.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<News> editorLoginEquals(String editorLogin) {
        return (root, query, cb) -> editorLogin == null || editorLogin.isBlank()
                ? cb.conjunction()
                : cb.equal(cb.lower(root.join("editor").get("login")), editorLogin.toLowerCase());
    }

    public static Specification<News> markerIdsIn(Collection<Long> markerIds) {
        return (root, query, cb) -> {
            if (markerIds == null || markerIds.isEmpty()) return cb.conjunction();
            query.distinct(true);
            Join<News, Marker> marker = root.join("markers");
            return marker.get("id").in(markerIds);
        };
    }

    public static Specification<News> markerNamesIn(Collection<String> markerNames) {
        return (root, query, cb) -> {
            if (markerNames == null || markerNames.isEmpty()) return cb.conjunction();
            query.distinct(true);
            Join<News, Marker> marker = root.join("markers");
            return cb.lower(marker.get("name")).in(markerNames.stream().map(String::toLowerCase).toList());
        };
    }
}
