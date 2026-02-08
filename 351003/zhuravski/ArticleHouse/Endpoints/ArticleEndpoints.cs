using ArticleHouse.Additions;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Endpoints;

public static class ArticleEndpoints
{
    private static readonly string GroupPrefix = "/api/v1.0/articles";

    public static void MapArticleEndpoints(this IEndpointRouteBuilder app)
    {
        var articleGroup = app.MapGroup(GroupPrefix).WithParameterValidation();

        articleGroup.MapGet("/", async (IArticleService service) =>
        {
            return Results.Ok(await service.GetAllArticlesAsync());
        });

        articleGroup.MapPost("/", async (IArticleService service, HttpContext context, ArticleRequestDTO dto) =>
        {
            ArticleResponseDTO result = await service.CreateArticleAsync(dto);
            string path = UrlRoutines.BuildAbsoluteUrl(context, $"{GroupPrefix}/{result.Id}");
            return Results.Created(path, result);
        });

        articleGroup.MapGet("/{id}", async (IArticleService service, long id) =>
        {
            return Results.Ok(await service.GetArticleByIdAsync(id));
        });
    }
}