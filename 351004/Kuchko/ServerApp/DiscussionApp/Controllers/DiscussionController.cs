using DiscussionApp.Repositories;
using Microsoft.AspNetCore.Mvc;
using SharedModels;

namespace DiscussionApp.Controllers;

[ApiController]
[Route("messages")]
public class DiscussionController(MessageRepository repo) : ControllerBase
{
    [HttpPost]
    public IActionResult Create([FromBody] MessageRequestTo request)
    {
        var newId = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        var msg = new MessageResponseTo(newId, request.ArticleId, request.Content, MessageState.Approve);
        repo.Create(msg);
        return Ok(msg);
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
        return msg == null ? NotFound() : Ok(msg);
    }

    [HttpPut("{id:long}")]
    public IActionResult Update(long id, [FromBody] MessageRequestTo request)
    {
        var msg = new MessageResponseTo(id, request.ArticleId, request.Content, MessageState.Approve);
        repo.Update(msg);
        return Ok(msg);
    }

    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        var existing = repo.GetById(id);
        if (existing == null) return NotFound();
        repo.Delete(id, existing.ArticleId);
        return NoContent();
    }
}