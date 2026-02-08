using ArticleHouse;
using ArticleHouse.ExcMiddleware;
using ArticleHouse.Endpoints;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddArticleHouseServices();

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

app.MapCreatorEndpoints();
app.MapArticleEndpoints();
app.MapCommentEndpoints();
app.MapMarkEndpoints();

app.Run();