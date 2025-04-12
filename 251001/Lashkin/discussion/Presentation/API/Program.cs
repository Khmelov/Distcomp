using API.Middlewares;
using Application.Extensions;
using Persistence.Extensions;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(24130);
});

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();

builder.Services.AddCors(options => options.AddPolicy("RestCors", policyBuilder => policyBuilder.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));
builder.Services.ConfigureAutoMapper();
builder.Services.ConfigureValidation();
builder.Services.ConfigureMediatR();
builder.Services.ConfigureMongoDb(builder.Configuration);
builder.Services.ConfigureUnitOfWork();
builder.Services.AddTransient<ExceptionHandlingMiddleware>();

var app = builder.Build();

app.UseMiddleware<ExceptionHandlingMiddleware>();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("RestCors");

app.MapControllers();

app.Run();
