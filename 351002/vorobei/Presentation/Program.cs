using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Profiles;
using BusinessLogic.Services;
using BusinessLogic.Servicies;
using DataAccess.Models;
using BusinessLogic.Repositories;
using Infrastructure.RepositoriesImplementation;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddSingleton<IRepository<Creator>, InMemoryRepository<Creator>>();
builder.Services.AddScoped<IBaseService<CreatorRequestTo, CreatorResponseTo>,
                           BaseService<Creator, CreatorRequestTo, CreatorResponseTo>>();
builder.Services.AddSingleton<IRepository<Mark>, InMemoryRepository<Mark>>();
builder.Services.AddScoped<IBaseService<MarkRequestTo, MarkResponseTo>,
                           BaseService<Mark, MarkRequestTo, MarkResponseTo>>();
builder.Services.AddSingleton<IRepository<Story>, InMemoryRepository<Story>>();
builder.Services.AddScoped<IBaseService<StoryRequestTo, StoryResponseTo>,
                           BaseService<Story, StoryRequestTo, StoryResponseTo>>();
builder.Services.AddSingleton<IRepository<Post>, InMemoryRepository<Post>>();
builder.Services.AddScoped<IBaseService<PostRequestTo, PostResponseTo>,
                           BaseService<Post, PostRequestTo, PostResponseTo>>();

builder.Services.AddAutoMapper(typeof(UserProfile));

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
