using DiscussionService.Application.DTOs.Requests;
using DiscussionService.Application.DTOs.Responses;
using DiscussionService.Application.Interfaces.Services;
using Microsoft.AspNetCore.Mvc;
using Shared.Controllers;

namespace DiscussionService.API.Controllers;

[ApiController]
[Route("api/v1.0/comments")]
public class CommentController : BaseController<long, CommentRequestToDto<long>, CommentResponseToDto<long>>
{
    private readonly ICommentService<long> _commentService;

    public CommentController(ICommentService<long> commentService) : base(commentService) 
    {
        _commentService = commentService;
    }

    // Переопределяем Get, чтобы тест видел 404 после удаления
    [HttpGet("{id}")]
    public override async Task<CommentResponseToDto<long>> Get(long id)
    {
        var result = await _commentService.GetAsync(id);
        
        if (result == null)
        {
            // Если твой BaseController или Middleware настроены стандартно,
            // выброс этой ошибки приведет к статусу 404 в ответе.
            throw new KeyNotFoundException($"Comment with id {id} not found");
        }

        return result;
    }
    
    [HttpDelete("{id}")]
    public override async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _service.DeleteAsync(id);
            return NoContent();
        }
        catch (KeyNotFoundException)
        {
            // Тест ожидает 4xx или 2xx при удалении, но NotFound здесь корректен
            return NotFound();
        }
    }
}