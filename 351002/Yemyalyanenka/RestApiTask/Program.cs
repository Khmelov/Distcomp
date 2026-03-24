using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ApplicationModels;
using Microsoft.AspNetCore.Mvc.Routing;
using RestApiTask.Infrastructure;
using RestApiTask.Mappings;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services;
using RestApiTask.Services.Interfaces;

namespace RestApiTask;

// Конвенция для добавления глобального префикса api/v1.0
public class RoutePrefixConvention : IApplicationModelConvention
{
    private readonly AttributeRouteModel _routePrefix;
    public RoutePrefixConvention(IRouteTemplateProvider route) => _routePrefix = new AttributeRouteModel(route);

    public void Apply(ApplicationModel application)
    {
        foreach (var controller in application.Controllers)
        {
            var selectors = controller.Selectors.Where(s => s.AttributeRouteModel != null).ToList();
            if (selectors.Any())
            {
                foreach (var selector in selectors)
                {
                    selector.AttributeRouteModel = AttributeRouteModel.CombineAttributeRouteModel(_routePrefix, selector.AttributeRouteModel);
                }
            }
            else
            {
                controller.Selectors.Add(new SelectorModel { AttributeRouteModel = _routePrefix });
            }
        }
    }
}

public class Program
{
    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);

        // Настройка контроллеров с глобальным префиксом
        builder.Services.AddControllers(options =>
        {
            options.Conventions.Insert(0, new RoutePrefixConvention(new RouteAttribute("api/v1.0")));
        });

        // Регистрация репозиториев (Singleton для InMemory)
        builder.Services.AddSingleton<IRepository<Writer>, InMemoryRepository<Writer>>();
        builder.Services.AddSingleton<IRepository<Article>, InMemoryRepository<Article>>();
        builder.Services.AddSingleton<IRepository<Marker>, InMemoryRepository<Marker>>();
        builder.Services.AddSingleton<IRepository<Message>, InMemoryRepository<Message>>();

        // Регистрация сервисов
        builder.Services.AddScoped<IWriterService, WriterService>();
        builder.Services.AddScoped<IArticleService, ArticleService>();
        builder.Services.AddScoped<IMarkerService, MarkerService>();
        builder.Services.AddScoped<IMessageService, MessageService>();

        // Ручная настройка AutoMapper 13
        var configExpression = new MapperConfigurationExpression();
        configExpression.AddProfile<MappingProfile>();
        var mapperConfig = new MapperConfiguration(configExpression);
        IMapper mapper = mapperConfig.CreateMapper();
        builder.Services.AddSingleton(mapper);

        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen();

        var app = builder.Build();

        // 1. Middleware обработки ошибок (ДОЛЖЕН БЫТЬ ПЕРВЫМ)
        app.UseMiddleware<ExceptionMiddleware>();

        // 2. Сидинг данных (Евгений Емельяненко)
        using (var scope = app.Services.CreateScope())
        {
            var writerRepo = scope.ServiceProvider.GetRequiredService<IRepository<Writer>>();
            writerRepo.AddAsync(new Writer
            {
                Login = "yevgeny2006@gmail.com",
                Firstname = "Евгений",
                Lastname = "Емельяненко",
                Password = "securePassword123"
            }).GetAwaiter().GetResult();
        }

        if (app.Environment.IsDevelopment())
        {
            app.UseSwagger();
            app.UseSwaggerUI();
        }

        app.UseAuthorization();
        app.MapControllers();
        app.Run();
    }
}