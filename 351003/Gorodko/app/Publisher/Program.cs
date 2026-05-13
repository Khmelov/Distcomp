using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using Publisher.Model;
using Publisher.Repository;
using Publisher.Service;
using System.IdentityModel.Tokens.Jwt;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers()
    .AddJsonOptions(options => {
        options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
        options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull;
    });

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c => {
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "Tweet API", Version = "v1.0" });
});

builder.Services.AddHttpClient("DiscussionClient", client => {
    client.BaseAddress = new Uri("http://localhost:24130");
    client.Timeout = TimeSpan.FromSeconds(30);
    client.DefaultRequestHeaders.Add("Accept", "application/json");
});

builder.Services.AddAutoMapper(typeof(Program));

builder.Services.AddScoped<IRepository<Editor>, EditorRepository>();
builder.Services.AddScoped<IRepository<Tweet>, TweetRepository>();
builder.Services.AddScoped<IStickerRepository, StickerRepository>();
builder.Services.AddScoped<IRepository<Sticker>, StickerRepository>();

builder.Services.AddScoped<EditorService>();
builder.Services.AddScoped<TweetService>();
builder.Services.AddScoped<StickerService>();
builder.Services.AddScoped<TokenService>();

builder.Services.AddSingleton<KafkaService>();
builder.Services.AddHostedService<KafkaResponseListener>();

builder.Services.AddStackExchangeRedisCache(options => {
    options.Configuration = builder.Configuration.GetConnectionString("Redis");
    options.InstanceName = "Publisher_";
});

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options => {
        options.TokenValidationParameters = new TokenValidationParameters {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = builder.Configuration["Jwt:Issuer"],
            ValidAudience = builder.Configuration["Jwt:Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"])),
        };
    });

builder.Services.AddAuthorization(options => {
    options.AddPolicy("AdminOnly", policy => policy.RequireClaim("role", "ADMIN"));
    options.AddPolicy("CustomerPolicy", policy => policy.RequireClaim("role", "ADMIN", "CUSTOMER"));
});

Dapper.DefaultTypeMap.MatchNamesWithUnderscores = true;

//builder.Services.AddTransient<GlobalExceptionHandler>();

/*builder.WebHost.ConfigureKestrel(options => {
    options.ListenLocalhost(24110);
});*/

var app = builder.Build();

if (app.Environment.IsDevelopment()) {
    app.UseSwagger();
    app.UseSwaggerUI(c => {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "Tweet API v1.0");
    });
}

//app.UseMiddleware<GlobalExceptionHandler>();

app.UseHttpsRedirection();
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

Console.WriteLine($"Starting module on port: {Environment.GetCommandLineArgs()}");

app.Run();

Console.WriteLine($"Configured URLs: {string.Join(", ", app.Urls)}");