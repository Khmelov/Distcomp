using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Infrastructure.Exceptions;
using Microsoft.AspNetCore.Mvc;

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
