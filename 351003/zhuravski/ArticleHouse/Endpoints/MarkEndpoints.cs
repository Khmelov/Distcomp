using System.ComponentModel.DataAnnotations;
using ArticleHouse.Additions;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Endpoints;

public static class MarkEndpoints
{
    private static readonly string GroupPrefix = "/api/v1.0/marks";

    public static void MapMarkEndpoints(this IEndpointRouteBuilder app)
    {
        var group = app.MapGroup(GroupPrefix).WithParameterValidation();

        group.MapGet("/", async (IMarkService service) =>
        {
            return Results.Ok(await service.GetAllMarksAsync());
        });

        group.MapPost("/", async (IMarkService service, HttpContext context, MarkRequestDTO dto) =>
        {
            MarkResponseDTO responseDTO = await service.CreateMarkAsync(dto);
            string path = UrlRoutines.BuildAbsoluteUrl(context, $"{GroupPrefix}/{responseDTO.Id}");
            return Results.Created(path, responseDTO);
        });

        group.MapDelete("/{id}", async (IMarkService service, long id) =>
        {
            await service.DeleteMarkAsync(id);
            return Results.NoContent();
        });

        group.MapGet("/{id}", async (IMarkService service, long id) =>
        {
            return Results.Ok(await service.GetMarkByIdAsync(id));
        });

        //Колхоз - и я не одобряю.
        //Qwen тоже не одобряет.
        group.MapPut("/", async (IMarkService service, MarkRequestDTO dto) =>
        {
            if (null == dto.Id)
            {
                throw new ValidationException("Mark identifier is missing.");
            }
            return Results.Ok(await service.UpdateMarkByIdAsync((long)dto.Id, dto));
        });
        
        group.MapPut("/{id}", async (IMarkService service, MarkRequestDTO dto, long id) =>
        {
            return Results.Ok(await service.UpdateMarkByIdAsync(id, dto));
        });
    }
}