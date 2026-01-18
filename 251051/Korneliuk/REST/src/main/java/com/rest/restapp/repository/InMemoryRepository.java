package com.rest.restapp.repository;

import com.rest.restapp.entity.User;
import com.rest.restapp.entity.Issue;
import com.rest.restapp.entity.Notice;
import com.rest.restapp.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InMemoryRepository {

    Map<Long, User> users = new ConcurrentHashMap<>();
    Map<Long, Issue> issues = new ConcurrentHashMap<>();
    Map<Long, Tag> tags = new ConcurrentHashMap<>();
    Map<Long, Notice> notices = new ConcurrentHashMap<>();

    AtomicLong userIdGenerator = new AtomicLong(1);
    AtomicLong issueIdGenerator = new AtomicLong(1);
    AtomicLong tagIdGenerator = new AtomicLong(1);
    AtomicLong noticeIdGenerator = new AtomicLong(1);

    public User saveUser(User user)
    {
        if (user.getId() == null) {
            user.setId(userIdGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUserById(Long id) {
        users.remove(id);
    }

    public boolean existsUserById(Long id) {
        return users.containsKey(id);
    }

    public Issue saveIssue(Issue issue) {
        if (issue.getId() == null) {
            issue.setId(issueIdGenerator.getAndIncrement());
        }
        if (issue.getModified() == null || issue.getCreated() == null) {
            var now = OffsetDateTime.now();
            issue.setCreated(now);
            issue.setModified(now);
        }
        issues.put(issue.getId(), issue);
        return issue;
    }

    public Optional<Issue> findIssueById(Long id) {
        return Optional.ofNullable(issues.get(id));
    }

    public List<Issue> findAllIssues() {
        return new ArrayList<>(issues.values());
    }

    public void deleteIssueById(Long id) {
        issues.remove(id);
    }

    public boolean existsIssueById(Long id) {
        return issues.containsKey(id);
    }

    public Tag saveTag(Tag tag) {
        if (tag.getId() == null) {
            tag.setId(tagIdGenerator.getAndIncrement());
        }
        tags.put(tag.getId(), tag);
        return tag;
    }

    public Optional<Tag> findTagById(Long id) {
        return Optional.ofNullable(tags.get(id));
    }

    public List<Tag> findAllTags() {
        return new ArrayList<>(tags.values());
    }

    public void deleteTagById(Long id) {
        tags.remove(id);
    }

    public boolean existsTagById(Long id) {
        return tags.containsKey(id);
    }

    public Notice saveNote(Notice notice) {
        if (notice.getId() == null) {
            notice.setId(noticeIdGenerator.getAndIncrement());
        }
        notices.put(notice.getId(), notice);
        return notice;
    }

    public Optional<Notice> findNoteById(Long id) {
        return Optional.ofNullable(notices.get(id));
    }

    public List<Notice> findAllNotes() {
        return new ArrayList<>(notices.values());
    }

    public void deleteNoteById(Long id) {
        notices.remove(id);
    }

    public boolean existsNoteById(Long id) {
        return notices.containsKey(id);
    }

}