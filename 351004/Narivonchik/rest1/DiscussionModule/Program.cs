using DiscussionModule.interfaces;
using DiscussionModule.mappers;
using DiscussionModule.persistence;
using DiscussionModule.persistence.repositories;
using DiscussionModule.services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddControllers();

// Register CassandraContext as singleton
builder.Services.AddSingleton<CassandraContext>(serviceProvider =>
{
    var configuration = serviceProvider.GetRequiredService<IConfiguration>();
    var context = new CassandraContext(configuration);
    
    context.CreateKeyspaceIfNotExists("distcompcasssandra");
    context.CreateTableIfNotExists();
    context.CreateCounterTableIfNotExists();
    
    return context;
});

builder.Services.AddScoped<INoteRepository, NoteRepository>();
builder.Services.AddScoped<INoteService, NoteService>();

// AutoMapper
builder.Services.AddAutoMapper(config =>
{
    config.AddProfile<NoteProfile>();
});

var app = builder.Build();
app.MapControllers();

app.Run();
