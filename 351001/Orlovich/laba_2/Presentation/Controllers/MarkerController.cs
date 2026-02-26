using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/markers")]
public class MarkerController: BaseController<MarkerResponseTo,MarkerRequestTo>
{
    public MarkerController(IService<MarkerResponseTo, MarkerRequestTo> service) : base(service)
    {
    }
}