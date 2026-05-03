namespace ServerApp.Models;

public record QueryParams (
    int PageNumber = 1,
    int PageSize = 10,
    string? SortBy = "Id",
    string? SortOrder = "asc"
);