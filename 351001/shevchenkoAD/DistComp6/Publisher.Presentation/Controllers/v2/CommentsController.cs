using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.Services.Interfaces;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Publisher.Presentation.Controllers.v2;

[ApiController]
[Route("api/v2.0/comments")]
[Authorize]
public class CommentsControllerV2 : ControllerBase
{
    private readonly ICommentService _commentService;

    public CommentsControllerV2(ICommentService commentService)
    {
        _commentService = commentService;
    }

    [HttpGet]
    [AllowAnonymous]
    public async Task<ActionResult<IEnumerable<CommentResponseTo>>> GetAll()
    {
        var result = await _commentService.GetAllAsync();
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    [AllowAnonymous]
    public async Task<ActionResult<CommentResponseTo>> GetById(long id)
    {
        var result = await _commentService.GetByIdAsync(id);
        return Ok(result);
    }

    [HttpPost]
    public async Task<ActionResult<CommentResponseTo>> Create([FromBody] CommentRequestTo request)
    {
        var result = await _commentService.CreateAsync(request);
        return StatusCode(201, result);
    }

    [HttpPut]
    public async Task<ActionResult<CommentResponseTo>> Update([FromBody] CommentRequestTo request)
    {
        var result = await _commentService.UpdateAsync(request);
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _commentService.DeleteAsync(id);
        return NoContent();
    }
}