using Microsoft.EntityFrameworkCore;
using RV.Models;
using AutoMapper;
using RV.Services.DataProviderServices;
using RV.Services.DataProviderServices.SQL;
using RV.Services.Mappers;
using RV.Repositories;
using RV.Repositories.SQLRepositories;
using RV.Services.DataProviderServices.Remote;
using Npgsql;

var builder = WebApplication.CreateBuilder(args);

var masterConnectionString = new NpgsqlConnectionStringBuilder();
masterConnectionString.Host = "localhost";
masterConnectionString.Port = 5432;
masterConnectionString.Username = "postgres";
masterConnectionString.Password = "Alex2405200417";
using (var connection = new NpgsqlConnection(masterConnectionString.ConnectionString))
{ 
    connection.Open();
    using (var command = new NpgsqlCommand())
    {
        command.Connection = connection;
        command.CommandText = $"SELECT 1 FROM pg_database WHERE datname='distcomp'";
        var result = command.ExecuteScalar();
        if (!(result != null && result != DBNull.Value))
        {
            command.CommandText = $"CREATE DATABASE \"distcomp\"";
            command.ExecuteNonQuery();
            command.CommandText = $"GRANT ALL PRIVILEGES ON DATABASE \"distcomp\" TO \"postgres\"";
            command.ExecuteNonQuery();
        }
    }
}
masterConnectionString.Database = "distcomp";
string connectionString = masterConnectionString.ConnectionString;
var dbContextOptions = new DbContextOptionsBuilder<ApplicationContext>()
                .UseNpgsql(connectionString).Options;
var _context = new ApplicationContext(dbContextOptions);
_context.Database.Migrate();
_context.Dispose();

builder.Services.AddDbContext<ApplicationContext>(options => options.UseNpgsql(connectionString));

builder.Services.AddTransient<IRepository<User>, SQLUserRepository>();
builder.Services.AddTransient<IRepository<Tweet>, SQLTweetRepository>();
builder.Services.AddTransient<IRepository<Note>, SQLNoteRepository>();
builder.Services.AddTransient<IRepository<Sticker>, SQLStickerRepository>();

builder.Services.AddTransient<IUserDataProvider, SQLUserDataProvider>();
builder.Services.AddTransient<ITweetDataProvider, SQLTweetDataProvider>();
//builder.Services.AddTransient<INoteDataProvider, NoSQLNoteDataProvider>();
builder.Services.AddTransient<INoteDataProvider, KafkaNoteDataProvider>();
builder.Services.AddTransient<IStickerDataProvider, SQLStickerDataProvider>();

builder.Services.AddTransient<IDataProvider, DataProvider>();

builder.Services.AddAutoMapper(typeof(UserMapper));
builder.Services.AddAutoMapper(typeof(NewsMapper));
builder.Services.AddAutoMapper(typeof(NoteMapper));
builder.Services.AddAutoMapper(typeof(StickerMapper));

builder.Services.AddStackExchangeRedisCache(options => {
    options.Configuration = "localhost:6379";
    options.InstanceName = "local";
});
// Add services to the container.
builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();

app.MapControllers();

app.Run();
