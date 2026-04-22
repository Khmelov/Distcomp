using Discussion;
using Discussion.Repository;
using Discussion.Service;
using Microsoft.OpenApi.Models;
using System.Text.Json;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers()
    .AddJsonOptions(options => {
        options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
        options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull;
    });

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c => {
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "Discussion API", Version = "v1.0" });
});

builder.Services.Configure<CassandraConfig>(
    builder.Configuration.GetSection("Cassandra"));

builder.Services.AddSingleton<IReactionRepository, CassandraReactionRepository>();

builder.Services.AddHttpClient();

builder.Services.AddAutoMapper(typeof(Program));

builder.Services.AddScoped<ReactionService>();
builder.Services.AddHostedService<KafkaService>();

builder.WebHost.ConfigureKestrel(options => {
    options.ListenLocalhost(24130);
});

Dapper.DefaultTypeMap.MatchNamesWithUnderscores = true;

var app = builder.Build();

if (app.Environment.IsDevelopment()) {
    app.UseSwagger();
    app.UseSwaggerUI(c => {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "Discussion API v1.0");
    });
}

app.UseRouting();
app.UseAuthorization();
app.MapControllers();

Console.WriteLine($"Starting module on port: {Environment.GetCommandLineArgs()}");

app.Run();

Console.WriteLine($"Configured URLs: {string.Join(", ", app.Urls)}");