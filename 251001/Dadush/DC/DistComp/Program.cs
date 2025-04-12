using AutoMapper;
using DistComp.Data;
using DistComp.Models;
using DistComp.Services;
using DistComp.Services.Implementation;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace DistComp {
    public class Program {

        private static readonly Dictionary<Type, (Type _in, Type _out)> mappedTypes = new() {
            { typeof(User), (typeof(UserInDto), typeof(UserOutDto)) },
            { typeof(Topic), (typeof(TopicInDto), typeof(TopicOutDto)) },
            { typeof(Tag), (typeof(TagInDto), typeof(TagOutDto)) },
            { typeof(Comment), (typeof(CommentInDto), typeof(CommentOutDto)) }
        };

        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddControllers();

            // Add Postgres DB provider
            builder.Services.AddDbContext<DCContext>(options => {
                options.UseNpgsql(builder.Configuration.GetConnectionString("postgres"));
            });

            // Add services
            builder.Services.AddTransient<UserService>();
            builder.Services.AddTransient<TopicService>();
            builder.Services.AddTransient<TagService>();
            builder.Services.AddTransient<ICommentService, CassandraCommentService>();

            // Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
            builder.Services.AddOpenApi();

            // Configure automapping
            builder.Services.AddSingleton(new MapperConfiguration(config => {
                foreach (var item in mappedTypes) {
                    config.CreateMap(item.Value._in, item.Key);
                    config.CreateMap(item.Key, item.Value._out);
                }
            }).CreateMapper());

            // User Swagger UI
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            // Use versioning
            builder.Services.AddApiVersioning();

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.MapOpenApi();

                app.UseSwagger();
                app.UseSwaggerUI();
            }
            
            app.UseAuthorization();


            app.MapControllers();

            app.Run();
        }
    }
}
