namespace discussion.Models.Domain;

public record NoticeEntity(long Id, long IssueId, string Content, string State);
