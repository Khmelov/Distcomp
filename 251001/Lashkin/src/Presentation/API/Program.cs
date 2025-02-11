using API.Middlewares;
using Application.Extensions;
using Persistence.Extensions;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options => options.AddPolicy("RestCors", policyBuilder => policyBuilder.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));
builder.Services.ConfigureRepositories();
builder.Services.AddTransient<ExceptionHandlingMiddleware>();
builder.Services.ConfigureMapper();
builder.Services.ConfigureMediatr();
builder.Services.ConfigureValidation();

var app = builder.Build();

app.UseMiddleware<ExceptionHandlingMiddleware>();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("RestCors");

app.MapControllers();

app.Run();
