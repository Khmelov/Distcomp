using Microsoft.AspNetCore.Mvc;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Services.Interfaces;

namespace ServerApp.Controllers;

[ApiController]
[Route("messages")]
public class MessageController(IMessageService messageService) : ControllerBase
{
    [HttpGet]
    public ActionResult<IEnumerable<MessageResponseTo>> GetAll() => Ok(messageService.GetAll());

    [HttpGet("{id:long}")]
    public ActionResult<MessageResponseTo> GetById(long id) => Ok(messageService.GetById(id));

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