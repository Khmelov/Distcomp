using Microsoft.AspNetCore.Diagnostics;
using Microsoft.EntityFrameworkCore;
using RV_Kisel_lab2_Task320.Models.Dtos;

namespace RV_Kisel_lab2_Task320.Exceptions;

public static class GlobalExceptionHandler {
    public static void UseCustomExceptionHandler(this IApplicationBuilder app) {
        app.UseExceptionHandler(err => {
            err.Run(async ctx => {
                var ex = ctx.Features.Get<IExceptionHandlerFeature>()?.Error;
                
                // Перехватываем исключения нарушения связей (DbUpdateException из-за внешних ключей или нашу ручную InvalidOperationException)
                if (ex is DbUpdateException || ex is InvalidOperationException) {
                    ctx.Response.StatusCode = 400; // Вот этот статус 400 и ждет тест!
                    await ctx.Response.WriteAsJsonAsync(new ErrorResponse { ErrorMessage = "Invalid Constraint", ErrorCode = "40002" });
                    return;
                }
                
                ctx.Response.StatusCode = 500;
                await ctx.Response.WriteAsJsonAsync(new ErrorResponse { ErrorMessage = "Error", ErrorCode = "50001" });
            });
        });
    }
}