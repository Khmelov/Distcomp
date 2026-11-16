package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Marker;
import by.bsuir.rest.entity.Message;
import by.bsuir.rest.entity.Story;
import by.bsuir.rest.entity.StoryMarker;
import by.bsuir.rest.entity.User;
import by.bsuir.rest.repository.MarkerFilter;
import by.bsuir.rest.repository.MessageFilter;
import by.bsuir.rest.repository.UserFilter;
import by.bsuir.rest.service.StorySearchCriteria;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

final class EntitySpecifications {

    private EntitySpecifications() {
    }

    static Specification<User> users(UserFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                equalText(predicates, builder, root.get("login"), filter.login());
                equalText(predicates, builder, root.get("firstname"), filter.firstname());
                equalText(predicates, builder, root.get("lastname"), filter.lastname());
            }
            return and(builder, predicates);
        };
    }

    static Specification<Marker> markers(MarkerFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                equalText(predicates, builder, root.get("name"), filter.name());
            }
            return and(builder, predicates);
        };
    }

    static Specification<Message> messages(MessageFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                if (filter.storyId() != null) {
                    predicates.add(builder.equal(root.get("storyId"), filter.storyId()));
                }
                equalText(predicates, builder, root.get("content"), filter.content());
            }
            return and(builder, predicates);
        };
    }

    static Specification<Story> stories(StorySearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria != null) {
                equalText(predicates, builder, root.get("title"), criteria.title());
                equalText(predicates, builder, root.get("content"), criteria.content());
                if (hasText(criteria.login())) {
                    Subquery<Long> userIds = query.subquery(Long.class);
                    Root<User> user = userIds.from(User.class);
                    userIds.select(user.get("id"))
                            .where(builder.equal(user.get("login"), criteria.login()));
                    predicates.add(root.get("userId").in(userIds));
                }
                if (criteria.markerIds() != null) {
                    for (Long markerId : criteria.markerIds()) {
                        if (markerId == null) {
                            continue;
                        }
                        Subquery<Long> storyIds = query.subquery(Long.class);
                        Root<StoryMarker> link = storyIds.from(StoryMarker.class);
                        storyIds.select(link.get("story").get("id"))
                                .where(builder.equal(link.get("markerId"), markerId));
                        predicates.add(root.get("id").in(storyIds));
                    }
                }
                if (criteria.markerNames() != null) {
                    for (String markerName : criteria.markerNames()) {
                        if (!hasText(markerName)) {
                            continue;
                        }
                        Subquery<Long> storyIds = query.subquery(Long.class);
                        Root<StoryMarker> link = storyIds.from(StoryMarker.class);
                        Root<Marker> marker = storyIds.from(Marker.class);
                        storyIds.select(link.get("story").get("id"))
                                .where(
                                        builder.equal(link.get("markerId"), marker.get("id")),
                                        builder.equal(marker.get("name"), markerName)
                                );
                        predicates.add(root.get("id").in(storyIds));
                    }
                }
            }
            return and(builder, predicates);
        };
    }

    private static void equalText(List<Predicate> predicates,
                                  jakarta.persistence.criteria.CriteriaBuilder builder,
                                  jakarta.persistence.criteria.Path<String> path,
                                  String value) {
        if (hasText(value)) {
            predicates.add(builder.equal(path, value));
        }
    }

    private static Predicate and(jakarta.persistence.criteria.CriteriaBuilder builder, List<Predicate> predicates) {
        if (predicates.isEmpty()) {
            return builder.conjunction();
        }
        return builder.and(predicates.toArray(Predicate[]::new));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
