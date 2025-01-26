using Persistence.Extensions;

var builder = WebApplication.CreateBuilder(args);

builder.Services.ConfigureRepositories();

var app = builder.Build();

app.Run();
