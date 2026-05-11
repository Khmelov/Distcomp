using DiscussionApp.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionApp.Controllers;

[ApiController]
[Route("messages")]
public class DiscussionController(MessageRepository repo) : ControllerBase
{
    [HttpPost]
    public IActionResult Create([FromBody] MessageRequest request)
    {
        // В реальном проекте ID генерирует Cassandra (uuid), но для совместимости с ТЗ генерим тут
        long newId = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds(); 
        repo.Create(request.ArticleId, newId, request.Content);
        return Ok(new { Id = newId, ArticleId = request.ArticleId, Content = request.Content });
    }

    [HttpGet("by-article/{articleId}")]
    public IActionResult GetByArticle(long articleId)
    {
        return Ok(repo.GetByArticle(articleId));
    }
}

public record MessageRequest(long ArticleId, string Content);