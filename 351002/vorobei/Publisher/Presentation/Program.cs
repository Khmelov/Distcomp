using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Profiles;
using BusinessLogic.Repositories;
using Infrastructure.ServiceImplementation;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Infrastructure.DatabaseContext;
using Infrastructure.RepositoriesImplementation;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<DistcompContext>(options =>
    options.UseNpgsql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        npgsqlOptions => npgsqlOptions
            .MigrationsAssembly("Infrastructure")
            .MigrationsHistoryTable("__EFMigrationsHistory", "public")
    ));

builder.Services.AddControllers();
builder.Services.AddScoped<IRepository<Creator>, EfCoreRepository<Creator>>();
builder.Services.AddScoped<IBaseService<CreatorRequestTo, CreatorResponseTo>,
                           CreatorService>();
builder.Services.AddScoped<IRepository<Mark>, EfCoreRepository<Mark>>();
builder.Services.AddScoped<IBaseService<MarkRequestTo, MarkResponseTo>,
                           BaseService<Mark, MarkRequestTo, MarkResponseTo>>();
builder.Services.AddScoped<IRepository<Story>, EfCoreRepository<Story>>();
builder.Services.AddScoped<IBaseService<StoryRequestTo, StoryResponseTo>>(provider =>
{
    var storyRepository = provider.GetRequiredService<IRepository<Story>>();
    var creatorRepository = provider.GetRequiredService<IRepository<Creator>>();
    var markRepository = provider.GetRequiredService<IRepository<Mark>>();
    var context = provider.GetRequiredService<DistcompContext>();
    var mapper = provider.GetRequiredService<IMapper>();
    return new StoryService(storyRepository, creatorRepository, markRepository, context, mapper);
});
builder.Services.AddHttpClient<IBaseService<PostRequestTo, PostResponseTo>, PostServiceProxy>();

builder.Services.AddSingleton(provider =>
{
    var config = new MapperConfiguration(
        cfg =>
        {
            cfg.AddProfile<UserProfile>();
        },
        provider.GetService<ILoggerFactory>()
    );

    return config.CreateMapper();
});

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<DistcompContext>();
    await dbContext.Database.MigrateAsync();
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
