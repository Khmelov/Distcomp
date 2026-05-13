namespace ServerApp.Models.DTOs;

public record ErrorResponse(
    string ErrorMessage, 
    int ErrorCode
);