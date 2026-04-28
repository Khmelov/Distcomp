namespace ServerApp.Models.DTOs.Responses;

public record AuthorResponseTo(
    long Id, 
    string Login, 
    string Firstname, 
    string Lastname
);