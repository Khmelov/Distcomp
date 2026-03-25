using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/notices")]
public class StoryController: BaseController<StoryResponseTo,StoryRequestTo>
{
    public StoryController(IService<StoryResponseTo, StoryRequestTo> service) : base(service)
    {
    }
}