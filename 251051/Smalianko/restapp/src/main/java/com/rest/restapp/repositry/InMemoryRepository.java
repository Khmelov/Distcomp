package com.rest.restapp.repositry;

import com.rest.restapp.entity.Author;
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

    Map<Long, Author> authors = new ConcurrentHashMap<>();
    Map<Long, Issue> issues = new ConcurrentHashMap<>();
    Map<Long, Tag> tags = new ConcurrentHashMap<>();
    Map<Long, Notice> notices = new ConcurrentHashMap<>();

    AtomicLong authorIdGenerator = new AtomicLong(1);
    AtomicLong issueIdGenerator = new AtomicLong(1);
    AtomicLong tagIdGenerator = new AtomicLong(1);
    AtomicLong noticeIdGenerator = new AtomicLong(1);

    public Author saveAuthor(Author author)
    {
        if (author.getId() == null) {
            author.setId(authorIdGenerator.getAndIncrement());
        }
        authors.put(author.getId(), author);
        return author;
    }

    public Optional<Author> findAuthorById(Long id) {
        return Optional.ofNullable(authors.get(id));
    }

    public List<Author> findAllAuthors() {
        return new ArrayList<>(authors.values());
    }

    public void deleteAuthorById(Long id) {
        authors.remove(id);
    }

    public boolean existsAuthorById(Long id) {
        return authors.containsKey(id);
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

    public Notice saveNotice(Notice notice) {
        if (notice.getId() == null) {
            notice.setId(noticeIdGenerator.getAndIncrement());
        }
        notices.put(notice.getId(), notice);
        return notice;
    }

    public Optional<Notice> findNoticeById(Long id) {
        return Optional.ofNullable(notices.get(id));
    }

    public List<Notice> findAllNotices() {
        return new ArrayList<>(notices.values());
    }

    public void deleteNoticeById(Long id) {
        notices.remove(id);
    }

    public boolean existsNoticeById(Long id) {
        return notices.containsKey(id);
    }

}