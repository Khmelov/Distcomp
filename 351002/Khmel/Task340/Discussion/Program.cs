using Discussion.Data;
using Discussion.Repositories;
using Discussion.Services;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls("http://localhost:24130");

// Cassandra
builder.Services.AddSingleton<CassandraContext>();
builder.Services.AddScoped<ICommentRepository, CommentRepository>();
builder.Services.AddScoped<ICommentService, CommentService>();

// Kafka
builder.Services.AddSingleton<IKafkaProducerService, KafkaProducerService>();
builder.Services.AddSingleton<IModerationService, ModerationService>();
builder.Services.AddHostedService<KafkaConsumerService>();

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<CassandraContext>();
    Console.WriteLine($" Connected to Cassandra: {context.Session.Cluster.Metadata.ClusterName}");
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();
app.UseAuthorization();
app.MapControllers();

Console.WriteLine(" Discussion microservice running on http://localhost:24130");
Console.WriteLine(" Kafka consumer listening to InTopic");
app.Run();