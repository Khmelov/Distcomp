using Application.Repository;
using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/issues")]
public class IssueController: BaseController<IssueResponseTo,IssueRequestTo>
{
    public IssueController(IService<IssueResponseTo, IssueRequestTo> service) : base(service)
    {
    }
}