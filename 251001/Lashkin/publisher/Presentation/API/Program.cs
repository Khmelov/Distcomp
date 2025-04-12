using API.Middlewares;
using Application.DTO.Request;
using Application.DTO.Response;
using Application.Extensions;
using External.API.Implementations;
using External.Contracts.Interfaces;
using Microsoft.EntityFrameworkCore;
using Persistence;
using Persistence.Extensions;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(24110);
});

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options => options.AddPolicy("RestCors", policyBuilder => policyBuilder.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));
builder.Services.AddTransient<ExceptionHandlingMiddleware>();
builder.Services.ConfigureRepositories(builder.Configuration);
builder.Services.ConfigureAutoMapper();
builder.Services.ConfigureValidation();
builder.Services.ConfigureMediatR();

builder.Services.AddHttpClient<INoticeExternalService<NoticeRequestTo, NoticeResponseTo>, NoticeExternalService>();

var app = builder.Build();

app.UseMiddleware<ExceptionHandlingMiddleware>();

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<RepositoryContext>();
    await context.Database.MigrateAsync();
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("RestCors");

app.MapControllers();

app.Run();
