using Microsoft.AspNetCore.Mvc;
using BusinessLogic.Servicies;
using BusinessLogic.DTO.Response;
using BusinessLogic.DTO.Request;
using DataAccess.Models;
using Infrastructure.Exceptions;

namespace Presentation.Controllers
{
    [Route("api/v1.0/[controller]")]
    [ApiController]
    public class CreatorsController : BaseController<Creator, CreatorRequestTo, CreatorResponseTo>
    {
        public CreatorsController(IBaseService<CreatorRequestTo, CreatorResponseTo> service) : base(service)
        {
        }
    }
}
