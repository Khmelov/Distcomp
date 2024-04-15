using Cassandra;
using Discussion.DTOs.Request;
using Discussion.Entities;
using Discussion.Repositories;
using Discussion.Services;
using Discussion.Services.Interfaces;
using Discussion.Services.Mappers;
using Discussion.Validators;

namespace Discussion
{
	public class Program
	{
		public static void Main(string[] args)
		{
			var builder = WebApplication.CreateBuilder(args);
			builder.WebHost.UseUrls("http://localhost:24130");

			// Add services to the container.

			builder.Services.AddControllers();
			// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
			builder.Services.AddEndpointsApiExplorer();
			builder.Services.AddSwaggerGen();

			builder.Services.AddScoped<IRepository<Note>, NoteDbRepository>();
			builder.Services.AddTransient<IValidator<NoteRequestTo>, NoteValidator>();
			builder.Services.AddTransient<INoteService, NoteService>();

			var cassandraContactPoints = "127.0.0.1"; 
			var cassandraKeyspace = "distcomp"; 
			var cassandraConnector = new CassandraConnector(cassandraContactPoints, cassandraKeyspace);
			var session = cassandraConnector.GetSession();
			builder.Services.AddSingleton(session);

			builder.Services.AddAutoMapper(typeof(NoteMapper));

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
		}
	}
}
