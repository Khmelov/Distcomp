using distcomp.Repositories;
using distcomp.Services;

var builder = WebApplication.CreateBuilder(args);

//adding services that are necessary for controllers and api
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

//registrating repositories
builder.Services.AddSingleton<CreatorRepository>();
builder.Services.AddSingleton<ArticleRepository>();
builder.Services.AddSingleton<TagRepository>();
builder.Services.AddSingleton<NoteRepository>();


//using scoped so a new service is created for each request
builder.Services.AddScoped<CreatorService>();
builder.Services.AddScoped<ArticleService>();
builder.Services.AddScoped<TagService>();
builder.Services.AddScoped<NoteService>();

var app = builder.Build();

//setting swagger for checking api's work during development
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
//getting detailed info abour errors during development
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}


//middleware setting
app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
