using System.ComponentModel.DataAnnotations;
using ArticleHouse.Additions;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Endpoints;

public static class CreatorEndpoints
{
    private static readonly string GroupPrefix = "/api/v1.0/creators";

    public static void MapCreatorEndpoints(this IEndpointRouteBuilder app)
    {
        var creatorGroup = app.MapGroup(GroupPrefix).WithParameterValidation();

        creatorGroup.MapGet("/", async (ICreatorService service) =>
        {
            return Results.Ok(await service.GetAllCreatorsAsync());
        });

        creatorGroup.MapPost("/", async (ICreatorService service, HttpContext context, CreatorRequestDTO dto) =>
        {
            CreatorResponseDTO responseDTO = await service.CreateCreatorAsync(dto);
            string path = UrlRoutines.BuildAbsoluteUrl(context, $"{GroupPrefix}/{responseDTO.Id}");
            return Results.Created(path, responseDTO);
        });

        creatorGroup.MapDelete("/{id}", async (ICreatorService service, long id) =>
        {
            await service.DeleteCreatorAsync(id);
            return Results.NoContent();
        });

        creatorGroup.MapGet("/{id}", async (ICreatorService service, long id) =>
        {
            return Results.Ok(await service.GetCreatorByIdAsync(id));
        });

        //Колхоз - и я не одобряю.
        //Qwen тоже не одобряет.
        creatorGroup.MapPut("/", async (ICreatorService service, CreatorRequestDTO dto) =>
        {
            if (null == dto.Id)
            {
                throw new ValidationException("Creator identifier is missing.");
            }
            return Results.Ok(await service.UpdateCreatorByIdAsync((long)dto.Id, dto));
        });
        
        creatorGroup.MapPut("/{id}", async (ICreatorService service, CreatorRequestDTO dto, long id) =>
        {
            return Results.Ok(await service.UpdateCreatorByIdAsync(id, dto));
        });
    }
}