using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/issues")]
public class NoteController: BaseController<NoteResponseTo,NoteRequestTo>
{
    public NoteController(IService<NoteResponseTo, NoteRequestTo> service) : base(service)
    {
    }
}