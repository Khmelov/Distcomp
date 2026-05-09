using Microsoft.AspNetCore.Diagnostics;

namespace Publisher.Exceptions {
    public class GlobalExceptionHandler : IExceptionHandler {
        private readonly ILogger<GlobalExceptionHandler> _logger;

        public GlobalExceptionHandler(ILogger<GlobalExceptionHandler> logger) {
            _logger = logger;
        }

        public async ValueTask<bool> TryHandleAsync(
            HttpContext httpContext,
            Exception exception,
            CancellationToken cancellationToken) {
            _logger.LogError(exception, "An unhandled exception occurred");

            var statusCode = exception switch {
                ValidationException => StatusCodes.Status400BadRequest,
                ForbiddenException => StatusCodes.Status403Forbidden,
                KeyNotFoundException => StatusCodes.Status404NotFound,
                _ => StatusCodes.Status500InternalServerError
            };

            var response = new {
                error = exception.Message,
                timestamp = DateTime.UtcNow
            };

            httpContext.Response.StatusCode = statusCode;
            await httpContext.Response.WriteAsJsonAsync(response, cancellationToken);

            return true;
        }
    }
}