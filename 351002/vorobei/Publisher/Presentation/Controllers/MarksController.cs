using Microsoft.AspNetCore.Mvc;
using BusinessLogic.Servicies;
using BusinessLogic.DTO.Response;
using BusinessLogic.DTO.Request;
using DataAccess.Models;

namespace Presentation.Controllers
{
    [Route("api/v1.0/[controller]")]
    [ApiController]
    public class MarksController : BaseController<Mark, MarkRequestTo, MarkResponseTo>
    {
        public MarksController(IBaseService<MarkRequestTo, MarkResponseTo> service) : base(service)
        {
        }
    }
}
