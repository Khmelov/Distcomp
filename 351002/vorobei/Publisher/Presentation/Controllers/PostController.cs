using Microsoft.AspNetCore.Mvc;
using DataAccess.Models;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;

namespace Presentation.Controllers
{
    [Route("api/v1.0/[controller]")]
    [ApiController]
    public class PostsController : BaseController<Post, PostRequestTo, PostResponseTo>
    {
        public PostsController(IBaseService<PostRequestTo, PostResponseTo> service) : base(service)
        {
        }
    }
}