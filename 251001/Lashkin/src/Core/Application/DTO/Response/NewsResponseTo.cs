namespace Application.DTO.Response;

public record NewsResponseTo(long Id, long UserId, string Title, string Content, DateTime Created, DateTime Modified);