using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);

// Add JWT Authentication
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = false,
        ValidateAudience = false,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(
            Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Secret"] ?? "YourSuperSecretKeyThatIsAtLeast32BytesLong!")),
        ClockSkew = TimeSpan.Zero,
        // Add these to help with debugging
        RequireExpirationTime = true,
        RequireSignedTokens = true
    };
    
    // Add event handler to see why tokens fail
    options.Events = new JwtBearerEvents
    {
        OnAuthenticationFailed = context =>
        {
            Console.WriteLine($"Authentication failed: {context.Exception.Message}");
            return Task.CompletedTask;
        },
        OnTokenValidated = context =>
        {
            Console.WriteLine("Token validated successfully");
            return Task.CompletedTask;
        }
    };
});

builder.Services.AddAuthorization();

builder.Services.AddControllers(options =>
{
    options.Filters.Add<GlobalExceptionFilter>();
});

// Add JWT Service
builder.Services.AddSingleton<IJwtService, JwtService>();

builder.Services.AddSingleton<IRepository<Writer>, WriterRepository>();
builder.Services.AddSingleton<IRepository<Story>, StoryRepository>();
builder.Services.AddSingleton<IRepository<Label>, LabelRepository>();
builder.Services.AddSingleton<IRepository<Comment>, CommentRepository>();

builder.Services.AddScoped<IWriterService, WriterService>();
builder.Services.AddScoped<IStoryService, StoryService>();
builder.Services.AddScoped<ILabelService, LabelService>();
builder.Services.AddScoped<ICommentService, CommentService>();

var app = builder.Build();

app.UseRouting();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

// Log startup
Console.WriteLine("Application starting on http://localhost:24110");
Console.WriteLine("JWT Secret configured");

app.Run();