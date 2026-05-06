using Discussion.Models;
using Discussion.Services;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Controllers;

[ApiController]
[Route("api/v1.0/posts")]
public class PostController : ControllerBase
{
    private readonly CassandraService _db;

    public PostController(CassandraService db)
    {
        _db = db;
    }

    [HttpGet("{id}")]
    public IActionResult Get(int id)
    {
        var post = _db.GetPost(id);
        if (post == null) return NotFound();
        return Ok(post);
    }

    [HttpPut]
    public IActionResult Put([FromBody] Post post)
    {
        _db.CreateOrUpdatePost(post);
        return Ok(post);
    }
}