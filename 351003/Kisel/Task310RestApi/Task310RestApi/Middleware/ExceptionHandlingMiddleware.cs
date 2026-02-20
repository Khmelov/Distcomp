
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using System.Text.Json;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Exceptions;

namespace Task310RestApi.Middleware
{
    public class ExceptionHandlingMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<ExceptionHandlingMiddleware> _logger;

        public ExceptionHandlingMiddleware(RequestDelegate next, ILogger<ExceptionHandlingMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (Exception ex)
            {
                await HandleExceptionAsync(context, ex);
            }
        }

        private async Task HandleExceptionAsync(HttpContext context, Exception exception)
        {
            _logger.LogError(exception, "Error handled by middleware");

            var statusCode = exception switch
            {
                ResourceNotFoundException => StatusCodes.Status404NotFound,
                ValidationException => StatusCodes.Status400BadRequest,
                _ => StatusCodes.Status500InternalServerError
            };

            var errorCode = exception is ValidationException vex ? vex.ErrorCode : (statusCode == 404 ? "40401" : "50000");

            context.Response.StatusCode = statusCode;
            context.Response.ContentType = "application/json";

            var errorResponse = new ErrorResponse(exception.Message, errorCode);

            // Используем Newtonsoft для сериализации, так как он подключен в Program.cs
            var json = Newtonsoft.Json.JsonConvert.SerializeObject(errorResponse, new Newtonsoft.Json.JsonSerializerSettings
            {
                ContractResolver = new Newtonsoft.Json.Serialization.CamelCasePropertyNamesContractResolver()
            });

            await context.Response.WriteAsync(json);
        }
    }
}
