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

    [HttpGet("{articleId}")]
    public IActionResult GetByArticle(long articleId)
    {
        return Ok(repo.GetByArticle(articleId));
    }

    [HttpGet]
    public IActionResult GetAll()
    {
        return Ok(repo.GetAll());
    }
    [HttpGet("{id:long}")]
    public IActionResult GetById(long id)
    {
        var msg = repo.GetById(id);
        if (msg == null) return NotFound();
        return Ok(msg);
    }

    [HttpPut("{id:long}")]
    public IActionResult Update(long id, [FromBody] MessageRequest request)
    {
        var existing = repo.GetById(id);
        if (existing == null) return NotFound();

        // По логике ТЗ, articleId привязан к сообщению, поэтому мы передаем его из запроса
        repo.Update(id, request.ArticleId, request.Content);
        
        return Ok(new { Id = id, ArticleId = request.ArticleId, Content = request.Content });
    }

    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        var existing = repo.GetById(id);
        if (existing == null) return NotFound();

        // Достаем articleId из существующего сообщения, так как он нужен для удаления в Cassandra
        long articleId = (long)existing.GetType().GetProperty("ArticleId").GetValue(existing, null);
        
        repo.Delete(id, articleId);
        return NoContent();
    }
    
}

public record MessageRequest(long ArticleId, string Content);