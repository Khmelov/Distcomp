using Discussion.Application.Services.Interfaces;
using Discussion.Presentation.Controllers.Abstractions;
using Microsoft.AspNetCore.Mvc;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Discussion.Presentation.Controllers;

public class CommentsController : BaseController<CommentRequestTo, CommentResponseTo>
{
    private readonly ICommentService _commentService;

    public CommentsController(ICommentService service) : base(service)
    {
        _commentService = service;
    }
    
    [HttpGet("issue/{issueId:long}")]
    public async Task<ActionResult<IEnumerable<CommentResponseTo>>> GetByIssueId(long issueId)
    {
        var result = await _commentService.GetByIssueIdAsync(issueId);
        return Ok(result);
    }
    
    [HttpDelete("issue/{issueId:long}")]
    public async Task<IActionResult> DeleteByIssueId(long issueId)
    {
        await _commentService.DeleteByIssueIdAsync(issueId);
        return NoContent();
    }
}