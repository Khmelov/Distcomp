using System.ComponentModel.DataAnnotations;
using ArticleHouse.DAO.CreatorDAO;
using ArticleHouse.ExcMiddleware;
using ArticleHouse.Service.CreatorService;

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
app.UseMiddleware<ExcMiddleware>();

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
    return Results.Ok(await service.UpdateCreatorByIdAsync((long)dto.Id, dto));
});
creatorGroup.MapPut("/{id}", async (ICreatorService service, CreatorRequestDTO dto, long id) =>
{
    return Results.Ok(await service.UpdateCreatorByIdAsync(id, dto));
});

app.Run();