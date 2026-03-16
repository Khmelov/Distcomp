using System.ComponentModel.DataAnnotations;
using ArticleHouse.Additions;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Endpoints;

public static class CommentEndpoints
{
    private static readonly string GroupPrefix = "/api/v1.0/comments";

    public static void MapCommentEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup(GroupPrefix).WithParameterValidation();

        group.MapGet("/", async (ICommentService service) =>
        {
            return Results.Ok(await service.GetAllCommentsAsync());
        });

        group.MapPost("/", async (ICommentService service, HttpContext context, CommentRequestDTO dto) =>
        {
            CommentResponseDTO responseDTO = await service.CreateCommentAsync(dto);
            string path = UrlRoutines.BuildAbsoluteUrl(context, $"{GroupPrefix}/{responseDTO.Id}");
            return Results.Created(path, responseDTO);
        });

        group.MapDelete("/{id}", async (ICommentService service, long id) =>
        {
            await service.DeleteCommentAsync(id);
            return Results.NoContent();
        });

        group.MapGet("/{id}", async (ICommentService service, long id) =>
        {
            return Results.Ok(await service.GetCommentByIdAsync(id));
        });

        //Колхоз - и я не одобряю.
        //Qwen тоже не одобряет.
        group.MapPut("/", async (ICommentService service, CommentRequestDTO dto) =>
        {
            if (null == dto.Id)
            {
                throw new ValidationException("Comment identifier is missing.");
            }
            return Results.Ok(await service.UpdateCommentByIdAsync((long)dto.Id, dto));
        });
        
        group.MapPut("/{id}", async (ICommentService service, CommentRequestDTO dto, long id) =>
        {
            return Results.Ok(await service.UpdateCommentByIdAsync(id, dto));
        });
    }
}