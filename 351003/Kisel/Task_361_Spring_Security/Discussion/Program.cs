using Discussion.Services;

var builder = WebApplication.CreateBuilder(args);

// Устанавливаем порт 24130 для микросервиса Discussion
builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenLocalhost(24130);
});

builder.Services.AddControllers();
builder.Services.AddSingleton<CassandraService>();
builder.Services.AddHostedService<KafkaConsumerService>();

var app = builder.Build();

app.MapControllers();

app.Run();