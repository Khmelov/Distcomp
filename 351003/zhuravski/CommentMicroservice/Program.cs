using Additions;
using CommentMicroservice;
using CommentMicroservice.Endpoints;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddCustomServices();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseMiddleware<ExcMiddleware>();

app.MapCommentEndpoints();

app.Run();