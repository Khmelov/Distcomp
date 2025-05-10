using System.Net;
using WebApplication1.Service;

namespace WebApplication1.Middleware
{
    public class ErrorHandlingMiddleware
    {
        private readonly RequestDelegate _next;

        public ErrorHandlingMiddleware(RequestDelegate next)
        {
            _next = next;
        }

        public async Task Invoke(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (ValidationException vex)
            {
                context.Response.StatusCode = vex.HttpCode; 
                await context.Response.WriteAsJsonAsync(new
                {
                    errorMessage = vex.Message,
                    errorCode = vex.ErrorCode
                });
            }
            catch (Exception ex)
            {
                context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                await context.Response.WriteAsJsonAsync(new
                {
                    errorMessage = ex.Message,
                    errorCode = "50000"
                });
            }
        }
    }
}
