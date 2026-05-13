using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers;

[ApiController]
[Route("api/v2.0/comments")]
[Authorize]
public class CommentsV2Controller : ControllerBase
{
    private readonly IAsyncService<CommentRequestDto, CommentResponseDto> _commentService;
    private readonly IValidator<CommentRequestDto> _commentValidator;

    public CommentsV2Controller(
        IAsyncService<CommentRequestDto, CommentResponseDto> commentService,
        IValidator<CommentRequestDto> commentValidator)
    {
        _commentService = commentService;
        _commentValidator = commentValidator;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<CommentResponseDto>>> GetComments() => Ok(await _commentService.GetAllAsync());

    [HttpGet("{id:long}")]
    public async Task<ActionResult<CommentResponseDto>> GetComment(long id)
    {
        var comment = await _commentService.ReadAsync(id);
        if (comment == null)
        {
            return NotFound(new ErrorResponseDto
            {
                ErrorMessage = "Comment not found.",
                ErrorCode = "40402"
            });
        }

        return Ok(comment);
    }

    [HttpPost]
    [Authorize(Roles = "ADMIN")]
    public async Task<ActionResult<CommentResponseDto>> CreateComment([FromBody] CommentRequestDto dto)
    {
        var validation = _commentValidator.Validate(dto);
        if (!validation.IsValid)
        {
            return BadRequest(new ErrorResponseDto
            {
                ErrorMessage = validation.Errors.First().ErrorMessage,
                ErrorCode = "40002"
            });
        }

        var created = await _commentService.CreateAsync(dto);
        return CreatedAtAction(nameof(GetComment), new { id = created.Id }, created);
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]
    public async Task<ActionResult<CommentResponseDto>> UpdateComment([FromBody] CommentRequestDto dto)
    {
        var updated = await _commentService.UpdateAsync(dto);
        return Ok(updated);
    }

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public async Task<ActionResult> DeleteComment(long id)
    {
        var deleted = await _commentService.DeleteAsync(id);
        return deleted ? NoContent() : NotFound(new ErrorResponseDto
        {
            ErrorMessage = "Comment not found.",
            ErrorCode = "40403"
        });
    }
}
