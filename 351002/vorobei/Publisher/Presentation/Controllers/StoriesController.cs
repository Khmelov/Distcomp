using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Microsoft.AspNetCore.Mvc;

namespace Presentation.Controllers
{
    [Route("api/v1.0/[controller]")]
    [ApiController]
    public class StoriesController : BaseController<Story, StoryRequestTo, StoryResponseTo>
    {
        public StoriesController(IBaseService<StoryRequestTo, StoryResponseTo> service) : base(service)
        {
        }
    }
}
