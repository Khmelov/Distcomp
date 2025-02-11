namespace Application.DTO.Request;

public record NewsRequestTo(long UserId, string Title, string Content, DateTime Created, DateTime Modified);