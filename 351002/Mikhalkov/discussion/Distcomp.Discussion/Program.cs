using Distcomp.Discussion.Infrastructure.Data;
using Distcomp.Discussion.Infrastructure.Repositories;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddSingleton<CassandraProvider>();
builder.Services.AddScoped<NoteRepository>();

var app = builder.Build();

try
{
    app.Services.GetRequiredService<CassandraProvider>();
    Console.WriteLine("Successful connection to Cassandra");
}
catch (Exception ex)
{
    Console.WriteLine($"Error initializing Cassandra: {ex.Message}");
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapControllers();

app.Run();