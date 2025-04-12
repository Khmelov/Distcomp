namespace Application.DTO.Request;

public record UpdateUserRequestTo(long Id, string Login, string Password, string FirstName, string LastName);