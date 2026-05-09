using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]s")]
    public abstract class BaseController : ControllerBase
    {
    }
}
