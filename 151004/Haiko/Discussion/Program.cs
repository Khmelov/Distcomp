using Microsoft.IdentityModel.Tokens;
using Discussion.Models;
using Discussion.Repositories;
using Discussion.Repositories.SQLRepositories;
using Discussion.Services.DataProviderServices;
using Discussion.Services.DataProviderServices.SQL;
using Discussion.Services.Kafka;
using Discussion.Services.Mappers;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddTransient<IRepository<Note>, NoSQLNoteRepository>();
builder.Services.AddTransient<INoteDataProvider, NoSQLNoteDataProvider>();
builder.Services.AddTransient<IKafkaCore, KafkaCore>();

builder.Services.AddAutoMapper(typeof(NoteMapper));

builder.Services.AddTransient<IDataProvider, DataProvider>();

builder.Services.AddControllers();

/*builder.Services.AddAuthentication("Bearer")
                .AddJwtBearer("Bearer", options =>
                {
                    options.Authority = "http://identity:8080";
                    options.RequireHttpsMetadata = false;
                    options.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidateAudience = false
                    };
                });
builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("ApiScope", policy =>
    {
        policy.RequireAuthenticatedUser();
        policy.RequireClaim("scope", "api1");
    });
});*/
var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

var kafka = app.Services.GetService<IKafkaCore>();
Thread kafkaThread = new Thread(kafka.StartConsuming);
kafkaThread.Start();

app.Run();
