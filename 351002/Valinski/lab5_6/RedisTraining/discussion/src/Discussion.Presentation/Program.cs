using Discussion.Presentation.DiConfiguration;
using Discussion.Presentation.Middlewares;

var builder = WebApplication.CreateBuilder(args);

builder.AddDependencies();

var app = builder.Build();

app.UseDependencies();

app.Run();
