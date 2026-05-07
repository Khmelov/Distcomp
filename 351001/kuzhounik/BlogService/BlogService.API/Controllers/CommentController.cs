using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using Microsoft.AspNetCore.Mvc;

namespace BlogService.API.Controllers;

[ApiController]
[Route("api/v1.0/comments")]
public class CommentController : BaseController<CommentRequestToDto, CommentResponseToDto>
{
    public CommentController(ICommentService service) : base(service) { }
}