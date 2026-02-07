using System.ComponentModel.DataAnnotations;
using System.Reflection;
using ArticleHouse.DAO.CreatorDAO;
using ArticleHouse.Service.CreatorService;
using ArticleHouse.Service.Exceptions;
using Microsoft.AspNetCore.Http.Json;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddSingleton<ICreatorDAO, MemoryCreatorDAO>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.UseStaticFiles("/static");

static async Task HandleException(HttpContext context, Exception e, int code)
{
    context.Response.StatusCode = code;
    context.Response.ContentType = "application/json";
    await context.Response.WriteAsJsonAsync(new {
        error = e.Message
    });
}

app.Use(async (HttpContext context, RequestDelegate next) =>
{
    try {
        await next(context);
    }
    catch (ServiceObjectNotFoundException e)
    {
        await HandleException(context, e, StatusCodes.Status404NotFound);
    }
    catch (ServiceException e)
    {
        await HandleException(context, e, StatusCodes.Status400BadRequest);
    }
    catch (ValidationException e)
    {
        await HandleException(context, e, StatusCodes.Status400BadRequest);
    }
});

app.MapGet("/", async (HttpContext context) =>
{
    context.Response.ContentType = "text/html";
    await context.Response.WriteAsync("<h1>Main page</h1>\n<img src=\"http://localhost:24110/static/img.jpg\">");
});

var v1Group = app.MapGroup("/api/v1.0");
var creatorGroup = v1Group.MapGroup("/creators").WithParameterValidation();
creatorGroup.MapGet("/", async (ICreatorService service) =>
{
    return Results.Ok(await service.GetAllCreatorsAsync());
});
creatorGroup.MapPost("/", async (ICreatorService service, CreatorRequestDTO dto) =>
{
    CreatorResponseDTO responseDTO = await service.CreateCreatorAsync(dto);
    return Results.Created($"/creators/{responseDTO.Id}", responseDTO);
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
    return Results.Ok();
});
creatorGroup.MapPut("/{id}", async (ICreatorService service, CreatorRequestDTO dto, long id) =>
{
    return Results.Ok();
});

app.Run();