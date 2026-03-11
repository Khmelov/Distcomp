using Microsoft.EntityFrameworkCore;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.application.mappers;
using rest1.application.services;
using rest1.infrastructure.persistence;
using rest1.persistence.db;
using rest1.persistence.db.repositories;

var builder = WebApplication.CreateBuilder(args);

// services for swagger ui
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// register controllers
builder.Services.AddControllers();

// register auto mappers
builder.Services.AddAutoMapper(
    config => {
        config.AddProfile<CreatorProfile>();
        config.AddProfile<NewsProfile>();
        config.AddProfile<MarkProfile>();
        config.AddProfile<NoteProfile>();
    });

// register in memory repositories
// builder.Services.AddSingleton<INewsRepository, NewsRepository>();
// builder.Services.AddSingleton<ICreatorRepository, CreatorRepository>();
// builder.Services.AddSingleton<IMarkRepository, MarkRepository>();
// builder.Services.AddSingleton<INoteRepository, NoteRepository>();

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<RestServiceDbContext>(options =>
    options.UseNpgsql(connectionString)
);

// register postgres db repositories
builder.Services.AddScoped<INewsRepository, DbNewsRepository>();
builder.Services.AddScoped<ICreatorRepository, DbCreatorRepository>();
builder.Services.AddScoped<IMarkRepository, DbMarkRepository>();
builder.Services.AddScoped<INoteRepository, DbNoteRepository>();

// register services
builder.Services.AddScoped<INewsService, NewsService>();
builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddScoped<IMarkService, MarkService>();
builder.Services.AddScoped<INoteService, NoteService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapGet("/", () =>
{
    return "API server is running.";
});

// map all controllers to http-routes
app.MapControllers();

app.Run();