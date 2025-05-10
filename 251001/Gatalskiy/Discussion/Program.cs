using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using MongoDB.Driver;
using System;
using Discussion;
using Discussion.Repositories;
using Discussion.Services;
using Discussion.Services.Kafka.Consumers;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.Configure<MongoDbSettings>(
    builder.Configuration.GetSection("MongoDbSettings"));

builder.Services.AddSingleton<IMongoClient>(serviceProvider =>
{
    var settings = builder.Configuration.GetSection("MongoDbSettings").Get<MongoDbSettings>();
    return new MongoClient(settings.ConnectionString);
});


builder.Services.AddSingleton<Discussion.Repositories.INoticeRepository, Discussion.Repositories.NoticeRepository>();
builder.Services.AddSingleton<Discussion.Services.INoticeService, Discussion.Services.NoticeService>();

builder.Services.AddAutoMapper(typeof(Program));
builder.Services.AddHostedService<KafkaDiscussionConsumerService>();
builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();