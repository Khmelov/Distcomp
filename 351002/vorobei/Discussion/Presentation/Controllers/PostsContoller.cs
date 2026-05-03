using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using Microsoft.AspNetCore.Mvc;

namespace Presentation.Controllers
{
    [Route("api/v1.0/[controller]")]
    [ApiController]
    public class PostsController : BaseController<PostRequestTo, PostResponseTo>
    {
        public PostsController(IBaseService<PostRequestTo, PostResponseTo> service)
            : base(service)
        {
        }
    }
}