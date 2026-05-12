using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;

namespace Publisher.Controllers
{
    public class BaseApiController : ControllerBase
    {
        [NonAction]
        protected ObjectResult Error(string message, int httpCode, int subCode = 1)
        {
            
            var response = new ErrorResponse(message, httpCode, subCode);
            return StatusCode(httpCode, response);
        }
    }
}