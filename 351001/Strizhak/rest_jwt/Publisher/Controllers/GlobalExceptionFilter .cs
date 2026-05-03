using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace Publisher.Controllers
{
    public class GlobalExceptionFilter : IExceptionFilter
    {
        private readonly ILogger<GlobalExceptionFilter> _logger;

        public GlobalExceptionFilter(ILogger<GlobalExceptionFilter> logger)
        {
            _logger = logger;
        }

        public void OnException(ExceptionContext context)
        {
            _logger.LogError(context.Exception, "Необработанное исключение");

            var response = context.Exception switch
            {
                KeyNotFoundException => new { error = "Ресурс не найден", code = 404 },
                ArgumentException => new { error = context.Exception.Message, code = 400 },
                _ => new { error = "Внутренняя ошибка сервера", code = 500 }
            };

            context.Result = new ObjectResult(response)
            {
                StatusCode = response.code
            };
            context.ExceptionHandled = true;
        }
    }
}
