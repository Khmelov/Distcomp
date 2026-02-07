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

app.Use(async (HttpContext context, RequestDelegate next) =>
{
    try {
        await next(context);
    }
    catch (ServiceObjectNotFoundException e)
    {
        context.Response.StatusCode = StatusCodes.Status404NotFound;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(e.Message);
    }
    catch (ServiceException e)
    {
        context.Response.StatusCode = StatusCodes.Status400BadRequest;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(e.Message);
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
    return Results.Ok();
});

app.Run();