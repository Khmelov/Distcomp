using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddSingleton<BaseService<AuthorRequestDto,AuthorResponseDto>,AuthorService>();
builder.Services.AddSingleton<BaseService<StoryRequestDto,StoryResponseDto>,StoryService>();
builder.Services.AddSingleton<BaseService<CommentRequestDto,CommentResponseDto>,CommentService>();
builder.Services.AddSingleton<BaseService<MarkerRequestDto,MarkerResponseDto>,MarkerService>();

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
