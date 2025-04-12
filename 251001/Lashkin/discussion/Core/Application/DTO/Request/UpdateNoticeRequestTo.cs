namespace Application.DTO.Request;

public record UpdateNoticeRequestTo(long Id, long NewsId, string Content);