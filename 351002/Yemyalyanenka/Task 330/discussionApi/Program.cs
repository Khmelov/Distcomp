using AutoMapper;
using Cassandra;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ApplicationModels;
using Microsoft.AspNetCore.Mvc.Routing;
using Microsoft.EntityFrameworkCore;
using RestApiTask.Data;
using RestApiTask.Infrastructure;
using RestApiTask.Mappings;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services;
using RestApiTask.Services.Interfaces;

namespace RestApiTask;

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
        builder.WebHost.UseUrls("http://localhost:24130");

        builder.Services.AddDbContext<AppDbContext>(options =>
        {
            options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres"));
        });

        builder.Services.AddControllers(options =>
        {
            options.Conventions.Insert(0, new RoutePrefixConvention(new RouteAttribute("api/v1.0")));
        });


        builder.Services.AddScoped<IRepository<Message>, CassandraRepository>();
        builder.Services.AddScoped<IMessageService, MessageService>();

        var configExpression = new MapperConfigurationExpression();
        configExpression.AddProfile<MappingProfile>();
        var mapperConfig = new MapperConfiguration(configExpression);
        IMapper mapper = mapperConfig.CreateMapper();
        builder.Services.AddSingleton(mapper);

        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen();

        try
        {
            var cluster = Cluster.Builder()
                .AddContactPoint("127.0.0.1")
                .Build();
            using var session = cluster.Connect();
            session.Execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");
            session.Execute("USE distcomp;");
            session.Execute("CREATE TABLE IF NOT EXISTS tbl_message (id bigint PRIMARY KEY, article_id bigint, country text, content text);");
            session.Execute("CREATE INDEX IF NOT EXISTS ON tbl_message (article_id);");
            Console.WriteLine("CASSANDRA: Keyspace and Table checked/created successfully.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"CASSANDRA ERROR: {ex.Message}");
        }

        var app = builder.Build();

        app.UseMiddleware<ExceptionMiddleware>();

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