using Microsoft.AspNetCore.Mvc;
using Project.Dto;

namespace Project.Controller {
    [ApiController]
    [Route("api/v1.0/[controller]")]
    [Produces("application/json")]
    public abstract class BaseController<TRequest, TResponse> : ControllerBase
    where TRequest : BaseRequestTo
    where TResponse : BaseResponseTo {
        protected readonly ILogger _logger;

        protected BaseController(ILogger logger) {
            _logger = logger;
        }

        protected ObjectResult CreateErrorResponse(int statusCode, string message) {
            return StatusCode(statusCode, new { error = message });
        }
    }
}