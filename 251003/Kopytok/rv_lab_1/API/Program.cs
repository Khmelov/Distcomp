using Application.abstractions;
using Application.services;
using Application.Services;
using Database;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddScoped<IEditorService, EditorService>();
builder.Services.AddScoped<INoteService, NoteService>();
builder.Services.AddScoped<IStoryService, StoryService>();
builder.Services.AddScoped<ITagService, TagService>();
builder.Services.AddScoped(typeof(IRepository<>), typeof(InMemoryRepository<>));

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
