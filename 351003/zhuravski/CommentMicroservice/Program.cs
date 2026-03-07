using Additions.Exceptions;
using Cassandra;
using CommentMicroservice;
using CommentMicroservice.Endpoints;

Cluster? cluster = Cluster.Builder()
                                .AddContactPoint("127.0.0.1")
                                .WithPort(9042)
                                .Build();
Cassandra.ISession? session = await cluster.ConnectAsync("distcomp");

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddCustomServices();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseMiddleware<ExcMiddleware>();

app.MapCommentEndpoints();

app.Run();