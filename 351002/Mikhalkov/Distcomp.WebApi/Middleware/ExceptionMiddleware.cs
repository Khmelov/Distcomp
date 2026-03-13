using Distcomp.Application.DTOs;
using Distcomp.Application.Exceptions;
using System.Net;
using System.Text.Json;

namespace Distcomp.WebApi.Middleware
{
	public class ExceptionMiddleware
	{
		private readonly RequestDelegate _next;
		private readonly ILogger<ExceptionMiddleware> _logger;

		public ExceptionMiddleware(RequestDelegate next, ILogger<ExceptionMiddleware> logger)
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
			catch (RestException ex)
			{
				await HandleExceptionAsync(context, ex.HttpStatusCode, ex.ErrorCode, ex.Message);
			}
			catch (Exception ex)
			{
				_logger.LogError(ex, "An unhandled exception occurred.");
				await HandleExceptionAsync(context, 500, 50000, "Internal Server Error");
			}
		}

		private static async Task HandleExceptionAsync(HttpContext context, int statusCode, int errorCode, string message)
		{
			context.Response.ContentType = "application/json";
			context.Response.StatusCode = statusCode;

			var response = new ErrorResponse(message, errorCode);

			var options = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
			var json = JsonSerializer.Serialize(response, options);

			await context.Response.WriteAsync(json);
		}
	}
}