using Application.DTOs.Responses;
using Application.Services;
using Application.Services.Interfaces;
using Application.Services.Profiles;
using Domain.Entities;
using Domain.Interfaces;
using Infrastructure;
using Infrastructure.Repositories;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using Presentation.Middleware;

namespace Presentation;

public class Program {
    public static void Main(string[] args) {
        var builder = WebApplication.CreateBuilder(args);

        builder.WebHost.UseUrls("http://localhost:24110");

        builder.Services.AddControllers()
            .ConfigureApiBehaviorOptions(options =>
            {
                options.InvalidModelStateResponseFactory = context =>
                {
                    var errorMessage = "Invalid data format in request body";
                    var errorResponse = new ErrorResponse(errorMessage, 40000); 
                    return new BadRequestObjectResult(errorResponse);
                };
            });

        builder.Services.AddEndpointsApiExplorer();

        builder.Services.AddSwaggerGen(options => {
            options.SwaggerDoc("v1", new OpenApiInfo {
                Title = "Task320 REST API",
                Version = "v1.0"
            });
        });
        
        var connectionString = builder.Configuration.GetConnectionString("PostgresDb");
        builder.Services.AddDbContext<AppDbContext>(options =>
                                                        options.UseNpgsql(connectionString));
        
        
        builder.Services.AddScoped<IRepository<Author>, DbAuthorRepository>();
        builder.Services.AddScoped<IIssueRepository,  DbIssueRepository>();
        builder.Services.AddScoped<IRepository<Issue>,  DbIssueRepository>();
        builder.Services.AddScoped<IRepository<Label>, DbLabelRepository>();
        builder.Services.AddScoped<IRepository<Comment>, DbCommentRepository>();

        builder.Services.AddScoped<IAuthorService, AuthorService>();
        builder.Services.AddScoped<IIssueService, IssueService>();
        builder.Services.AddScoped<ILabelService, LabelService>();
        builder.Services.AddScoped<ICommentService, CommentService>();

        builder.Services.AddAutoMapper(cfg => { cfg.AddProfile<MappingProfile>(); });

        var app = builder.Build();
        
        using (var scope = app.Services.CreateScope())
        {
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            db.Database.Migrate();
        }

        app.UseSwagger();
        app.UseSwaggerUI(options => { options.SwaggerEndpoint("/swagger/v1/swagger.json", "Task320 API v1.0"); });

        app.UseMiddleware<ExceptionHandlingMiddleware>();
        app.MapControllers();

        app.Run();
    }
}