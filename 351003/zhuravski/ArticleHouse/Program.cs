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
app.MapGet("/", async (HttpContext context) =>
{
    context.Response.ContentType = "text/html";
    await context.Response.WriteAsync("<h1>Main page</h1>\n<img src=\"http://localhost:24110/static/img.jpg\">");
});

var v1Group = app.MapGroup("/api/v1.0");
var creatorGroup = v1Group.MapGroup("/creators").WithParameterValidation();
creatorGroup.MapGet("/", async (ICreatorService service) =>
{
    try
    {
        return Results.Ok(await service.GetAllCreatorsAsync());
    }
    catch (ServiceException e)
    {
        return Results.BadRequest(e.Message);
    }
});
creatorGroup.MapPost("/", async (ICreatorService service, CreatorRequestDTO dto) =>
{
    try {
        CreatorResponseDTO responseDTO = await service.CreateCreatorAsync(dto);
        return Results.Created($"/creators/{responseDTO.Id}", responseDTO);
    }
    catch (ServiceException e)
    {
        return Results.BadRequest(e.Message);
    }
});

app.Run();