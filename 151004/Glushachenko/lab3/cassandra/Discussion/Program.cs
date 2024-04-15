using Discussion.Middleware;
using Discussion.PostEntity;
using Discussion.PostEntity.Interface;
using Discussion.Storage.Cassandra;
using MyCoolMapper = Discussion.Common.AutoMapper;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllers().AddNewtonsoftJson();
builder.Services.AddSingleton<Random>();
builder.Services
    .AddScoped<CassandraStorage>()
    .AddScoped<IPostRepository, PostRepository>()
    .AddScoped<IPostService, PostService>();
builder.Services.AddAutoMapper(typeof(MyCoolMapper));

var app = builder.Build();
app.MapControllers();
app.UseURLLog();
app.Run();
