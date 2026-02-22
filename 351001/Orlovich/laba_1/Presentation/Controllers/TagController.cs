using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/markers")]
public class TagController: BaseController<TagResponseTo,TagRequestTo>
{
    public TagController(IService<TagResponseTo, TagRequestTo> service) : base(service)
    {
    }
}