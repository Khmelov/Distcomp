using Microsoft.AspNetCore.Mvc;
using ServerApp.Models.DTOs;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Services.Interfaces;

namespace ServerApp.Controllers;

[ApiController]
[Route("articles")]
public class ArticleController(IArticleService articleService) : ControllerBase
{
    [HttpGet]
    public ActionResult<IEnumerable<ArticleResponseTo>> GetAll() => Ok(articleService.GetAll());

    [HttpGet("{id:long}")]
    public ActionResult<ArticleResponseTo> GetById(long id) => Ok(articleService.GetById(id));

    [HttpPost]
    public ActionResult<ArticleResponseTo> Create([FromBody] ArticleRequestTo request)
    {
        var response = articleService.Create(request);
        return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
    }

    [HttpPut("{id:long}")]
    [HttpPut]
    public ActionResult<ArticleResponseTo> Update(long? id, [FromBody] ArticleRequestTo request)
    {
        long finalId = id ?? (request.Id ?? 0);
        
        if (finalId == 0)
        {
            return BadRequest(new ErrorResponse("ID must be provided in URL or body", 40002));
        }
        
        return Ok(articleService.Update(finalId, request));
    }

    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        articleService.Delete(id);
        return NoContent();
    }
}