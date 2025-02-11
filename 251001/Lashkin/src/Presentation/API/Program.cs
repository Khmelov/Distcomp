using Persistence.Extensions;

var builder = WebApplication.CreateBuilder(args);

builder.Services.ConfigureRepositories();
builder.Services.ConfigureValidation();

var app = builder.Build();

app.Run();
