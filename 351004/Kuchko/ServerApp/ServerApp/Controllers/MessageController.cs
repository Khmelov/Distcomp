using Microsoft.AspNetCore.Mvc;
using ServerApp.Services.Interfaces;
using SharedModels;

namespace ServerApp.Controllers;

[ApiController]
[Route("messages")]
public class MessageController(IMessageService messageService) : ControllerBase
{
    [HttpGet]
    public ActionResult<IEnumerable<MessageResponseTo>> GetAll()
    {
        return Ok(messageService.GetAll());
    }

    [HttpGet("{id:long}")]
    public ActionResult<MessageResponseTo> GetById(long id)
    {
        return Ok(messageService.GetById(id));
    }

    [HttpPost]
    public ActionResult<MessageResponseTo> Create([FromBody] MessageRequestTo request)
    {
        var response = messageService.Create(request);
        return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
    }

    [HttpPut("{id:long}")]
    public ActionResult<MessageResponseTo> Update(long id, [FromBody] MessageRequestTo request)
    {
        return Ok(messageService.Update(id, request));
    }

    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        messageService.Delete(id);
        return NoContent();
    }
}