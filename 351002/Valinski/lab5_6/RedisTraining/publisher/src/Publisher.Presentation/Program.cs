using Publisher.Presentation.DiConfiguration;
using Publisher.Presentation.Middlewares;

var builder = WebApplication.CreateBuilder(args);

builder.AddDependencies();

var app = builder.Build();

app.UseExtension();

app.Run();
