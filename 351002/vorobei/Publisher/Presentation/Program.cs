using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Profiles;
using BusinessLogic.Repositories;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Infrastructure.DatabaseContext;
using Infrastructure.Kafka;
using Infrastructure.RepositoriesImplementation;
using Infrastructure.ServiceImplementation;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("YourSuperSecretKeyThatIsAtLeast32BytesLong!")),
            RoleClaimType = "role"
        };
    });

builder.Services.AddAuthorization();

builder.Services.AddStackExchangeRedisCache(options =>
{
    options.Configuration = "localhost:6379";
    options.InstanceName = "distcomp_";
});

builder.Services.AddSingleton<IModerationResultWaiter, ModerationResultWaiter>();
builder.Services.AddSingleton<PostModerationProducer>();
builder.Services.AddHostedService<PostModerationResultConsumer>();

builder.Services.AddDbContext<DistcompContext>(options =>
    options.UseNpgsql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        npgsqlOptions => npgsqlOptions
            .MigrationsAssembly("Infrastructure")
            .MigrationsHistoryTable("__EFMigrationsHistory", "public")
    ));

builder.Services.AddControllers();

builder.Services.AddScoped<IRepository<Creator>, EfCoreRepository<Creator>>();
builder.Services.AddScoped<IBaseService<CreatorRequestTo, CreatorResponseTo>, CreatorService>();
builder.Services.AddScoped<IRepository<Mark>, EfCoreRepository<Mark>>();
builder.Services.AddScoped<IBaseService<MarkRequestTo, MarkResponseTo>, BaseService<Mark, MarkRequestTo, MarkResponseTo>>();
builder.Services.AddScoped<IRepository<Story>, EfCoreRepository<Story>>();
builder.Services.AddScoped<IBaseService<StoryRequestTo, StoryResponseTo>>(provider =>
{
    var storyRepository = provider.GetRequiredService<IRepository<Story>>();
    var creatorRepository = provider.GetRequiredService<IRepository<Creator>>();
    var markRepository = provider.GetRequiredService<IRepository<Mark>>();
    var context = provider.GetRequiredService<DistcompContext>();
    var mapper = provider.GetRequiredService<IMapper>();
    var cache = provider.GetRequiredService<IDistributedCache>();

    return new StoryService(storyRepository, creatorRepository, markRepository, mapper, cache);
});
builder.Services.AddScoped<IBaseService<PostRequestTo, PostResponseTo>, PostService>();

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
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

app.Run();