package com.example.rv.impl.issue;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class IssueMapperImpl implements IssueMapper {
    @Override
    public IssueRequestTo issueToRequestTo(Issue issue) {
        return new IssueRequestTo(
                issue.getId(),
                issue.getCreatorId(),
                issue.getTitle(),
                issue.getContent(),
                issue.getCreated(),
                issue.getModified()
        );
    }

    @Override
    public List<IssueRequestTo> issueToRequestTo(Iterable<Issue> issues) {
        return StreamSupport.stream(issues.spliterator(), false)
                .map(this::issueToRequestTo)
                .collect(Collectors.toList());
    }

    @Override
    public Issue dtoToEntity(IssueRequestTo issueRequestTo) {
        return new Issue(
                issueRequestTo.id(),
                issueRequestTo.creatorId(),
                issueRequestTo.title(),
                issueRequestTo.content(),
                issueRequestTo.created(),
                issueRequestTo.modified());
    }

    @Override
    public List<Issue> dtoToEntity(Iterable<IssueRequestTo> issueRequestTos) {
        return StreamSupport.stream(issueRequestTos.spliterator(), false)
                .map(this::dtoToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public IssueResponseTo issueToResponseTo(Issue issue) {
        return new IssueResponseTo(
                issue.getId(),
                issue.getCreatorId(),
                issue.getTitle(),
                issue.getContent(),
                issue.getCreated(),
                issue.getModified()
        );
    }

    @Override
    public List<IssueResponseTo> issueToResponseTo(Iterable<Issue> issues) {
        return StreamSupport.stream(issues.spliterator(), false)
                .map(this::issueToResponseTo)
                .collect(Collectors.toList());
    }
}
