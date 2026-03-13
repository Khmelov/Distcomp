using DistComp.Application.Services;
using DistComp.Application.Services.Interfaces;
using DistComp.Application.Services.Profiles;
using DistComp.Domain.Entities;
using DistComp.Domain.Interfaces;
using DistComp.Infrastructure.Repositories;
using DistComp.Presentation.Middleware;
using Microsoft.OpenApi.Models;

namespace DistComp;

public class Program {
    public static void Main(string[] args) {
        var builder = WebApplication.CreateBuilder(args);

        builder.WebHost.UseUrls("http://localhost:24110");

        builder.Services.AddControllers()
            .ConfigureApiBehaviorOptions(options => { options.SuppressModelStateInvalidFilter = true; });


        builder.Services.AddEndpointsApiExplorer();

        builder.Services.AddSwaggerGen(options => {
            options.SwaggerDoc("v1", new OpenApiInfo {
                Title = "Task310 REST API",
                Version = "v1.0"
            });
        });

        builder.Services.AddSingleton<IRepository<Author>, LocalAuthorRepository>();
        builder.Services.AddSingleton<IRepository<Issue>, LocalIssueRepository>();
        builder.Services.AddSingleton<IRepository<Label>, LocalLabelRepository>();
        builder.Services.AddSingleton<IRepository<Comment>, LocalCommentRepository>();

        builder.Services.AddScoped<IAuthorService, AuthorService>();
        builder.Services.AddScoped<IIssueService, IssueService>();
        builder.Services.AddScoped<ILabelService, LabelService>();
        builder.Services.AddScoped<ICommentService, CommentService>();

        builder.Services.AddAutoMapper(cfg => { cfg.AddProfile<MappingProfile>(); });

        var app = builder.Build();

        app.UseSwagger();
        app.UseSwaggerUI(options => { options.SwaggerEndpoint("/swagger/v1/swagger.json", "Task310 API v1.0"); });

        app.UseMiddleware<ExceptionHandlingMiddleware>();
        app.MapControllers();

        app.Run();
    }
}