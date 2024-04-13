package com.example.rv.impl.issue;

import java.util.List;

public interface IssueMapper {

    IssueRequestTo issueToRequestTo(Issue issue);

    List<IssueRequestTo> issueToRequestTo(Iterable<Issue> issues);

    Issue dtoToEntity(IssueRequestTo issueRequestTo);

    List<Issue> dtoToEntity(Iterable<IssueRequestTo> issueRequestTos);

    IssueResponseTo issueToResponseTo(Issue issue);

    List<IssueResponseTo> issueToResponseTo(Iterable<Issue> issues);
}
