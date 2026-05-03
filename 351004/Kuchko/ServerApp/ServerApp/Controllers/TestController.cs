using Microsoft.AspNetCore.Mvc;

namespace ServerApp.Controllers;

[ApiController]
[Route("test")] // Итоговый путь будет: /api/v1.0/test
public class TestController : ControllerBase
{
    [HttpGet]
    public IActionResult GetStatus()
    {
        return Ok(new 
        { 
            Message = "Приложение работает!", 
            Version = "1.0", 
            Time = DateTime.Now 
        });
    }
}