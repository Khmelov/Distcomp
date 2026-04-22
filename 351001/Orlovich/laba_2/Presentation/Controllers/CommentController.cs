using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/comments")]
public class CommentController: BaseController<CommentResponseTo,CommentRequestTo>
{
    public CommentController(IService<CommentResponseTo, CommentRequestTo> service) : base(service)
    {
    }
}