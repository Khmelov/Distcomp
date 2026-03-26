using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json.Serialization;
using RV_Kisel_lab2_Task320.Data;
using RV_Kisel_lab2_Task320.Exceptions;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Services;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls("http://0.0.0.0:24110");

builder.Services.AddDbContext<AppDbContext>(opt => 
    opt.UseNpgsql(builder.Configuration.GetConnectionString("PostgresDb")));

builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddScoped<INewsService, NewsService>();
builder.Services.AddScoped<ILabelService, LabelService>();

builder.Services.AddHttpClient<IDiscussionServiceClient, DiscussionServiceClient>(client =>
{
    client.BaseAddress = new Uri("http://localhost:24130"); 
});

// Регистрация Kafka
builder.Services.AddSingleton<KafkaProducerService>();
builder.Services.AddHostedService<KafkaConsumerService>();

builder.Services.AddControllers()
    // ВОТ ЭТА СТРОКА ЖИЗНЕННО НЕОБХОДИМА! Она заставит lab4 увидеть Creator, News и Label
    .AddApplicationPart(typeof(RV_Kisel_lab2_Task320.Controllers.CreatorController).Assembly)
    .ConfigureApiBehaviorOptions(options =>
    {
        options.InvalidModelStateResponseFactory = context =>
        {
            var result = new BadRequestObjectResult(new ErrorResponse 
            { 
                ErrorMessage = "Invalid Request Body", 
                ErrorCode = "40001" 
            });
            return result;
        };
    })
    .AddNewtonsoftJson(options => {
        options.SerializerSettings.ContractResolver = new CamelCasePropertyNamesContractResolver();
    });

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    dbContext.Database.EnsureCreated();
}

app.UseCustomExceptionHandler(); 

if (app.Environment.IsDevelopment()) {
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseRouting();
app.MapControllers();

app.Run();