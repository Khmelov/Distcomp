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
builder.Services.AddScoped<IPostService, PostService>();
builder.Services.AddScoped<ILabelService, LabelService>();

// Настройка контроллеров и ВАЛИДАЦИИ
builder.Services.AddControllers()
    .ConfigureApiBehaviorOptions(options =>
    {
        // Переопределяем стандартную 400 ошибку ASP.NET, чтобы она возвращала наш ErrorResponse (важно для тестов!)
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

// АВТОМАТИЧЕСКОЕ СОЗДАНИЕ ТАБЛИЦ И СХЕМЫ В POSTGRES
// АВТОМАТИЧЕСКОЕ СОЗДАНИЕ ТАБЛИЦ И СХЕМЫ В POSTGRES
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    // Оставляем только эту строчку:
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