using DiscussionApp.Repositories;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ApplicationModels;
using Microsoft.AspNetCore.Mvc.Routing;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<MessageRepository>();

builder.Services.AddControllers(options =>
{
    // Обязательное требование ТЗ: префикс /api/v1.0/

    options.Conventions.Add(new ApiPrefixConvention(new RouteAttribute("api/v1.0")));
});

// --- 3. НАСТРОЙКА СВОБОДНОГО CORS (Для локального тестирования) ---
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy => { policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader(); });
});

builder.Services.AddOpenApi();

var app = builder.Build();

if (app.Environment.IsDevelopment()) app.MapOpenApi();

app.UseCors();
app.UseAuthorization();
app.MapControllers();

app.Run();

public class ApiPrefixConvention(IRouteTemplateProvider route) : IApplicationModelConvention
{
    private readonly AttributeRouteModel _routePrefix = new(route);

    public void Apply(ApplicationModel application)
    {
        foreach (var selector in application.Controllers.SelectMany(c => c.Selectors))
            if (selector.AttributeRouteModel != null)
                selector.AttributeRouteModel =
                    AttributeRouteModel.CombineAttributeRouteModel(_routePrefix, selector.AttributeRouteModel);
            else
                selector.AttributeRouteModel = _routePrefix;
    }
}