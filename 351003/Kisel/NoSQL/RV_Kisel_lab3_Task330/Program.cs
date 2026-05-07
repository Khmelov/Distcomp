using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json.Serialization;
using RV_Kisel_lab2_Task320.Data;
using RV_Kisel_lab2_Task320.Exceptions;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Services;

var builder = WebApplication.CreateBuilder(args);

// Порт 24110 из требований
builder.WebHost.UseUrls("http://localhost:24110");

// Подключение к БД
builder.Services.AddDbContext<AppDbContext>(opt => 
    opt.UseNpgsql(builder.Configuration.GetConnectionString("PostgresDb")));

// Регистрация сервисов
builder.Services.AddScoped<ICreatorService, CreatorService>();
builder.Services.AddScoped<INewsService, NewsService>();
builder.Services.AddScoped<ILabelService, LabelService>();

// !!! --- ИСПРАВЛЕНИЕ: РЕГИСТРАЦИЯ HTTP КЛИЕНТА ПЕРЕМЕЩЕНА СЮДА --- !!!
builder.Services.AddHttpClient<IDiscussionServiceClient, DiscussionServiceClient>(client =>
{
    // Адрес нашего нового микросервиса
    client.BaseAddress = new Uri("http://localhost:24130"); 
});

// Настройка контроллеров и ВАЛИДАЦИИ
builder.Services.AddControllers()
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
builder.Services.AddHttpClient();

// !!! --- ВСЕ РЕГИСТРАЦИИ СЕРВИСОВ ДОЛЖНЫ БЫТЬ ВЫШЕ ЭТОЙ СТРОКИ --- !!!
var app = builder.Build();

// АВТОМАТИЧЕСКОЕ СОЗДАНИЕ ТАБЛИЦ И СХЕМЫ В POSTGRES
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