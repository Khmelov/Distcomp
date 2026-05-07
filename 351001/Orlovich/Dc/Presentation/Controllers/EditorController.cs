using Application.Repository;
using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/authors")]
public class EditorController : BaseController<EditorResponseTo,EditorRequestTo>
{
    public EditorController(IService<EditorResponseTo, EditorRequestTo> service) : base(service)
    {
    }
}