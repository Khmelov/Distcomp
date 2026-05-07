using Application.Mapper;
using Application.Repository;
using Application.Service;
using Dc.Middleware;
using Domain.Models;
using Infrastructe.Repository;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<IRepository<Editor>,LocalEditorRepository>();
builder.Services.AddScoped<IService<EditorResponseTo,EditorRequestTo>,EditorService>();

builder.Services.AddSingleton<IRepository<Story>,LocalStoryRepository>();
builder.Services.AddScoped<IService<StoryResponseTo,StoryRequestTo>,StoryService>();

builder.Services.AddSingleton<IRepository<Tag>,LocalTagRepository>();
builder.Services.AddScoped<IService<TagResponseTo,TagRequestTo>,TagService>();

builder.Services.AddSingleton<IRepository<Note>,LocalNoteRepository>();
builder.Services.AddScoped<IService<NoteResponseTo,NoteRequestTo>,NoteService>();

builder.Services.AddAutoMapper(typeof(MappingProfile).Assembly);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

var app = builder.Build();


app.UseMiddleware<HandleErrorMiddleware>(); 

app.UseHttpsRedirection();
app.MapControllers();

app.Run();