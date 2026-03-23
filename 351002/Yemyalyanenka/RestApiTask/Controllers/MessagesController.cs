using Microsoft.AspNetCore.Mvc;
using RestApiTask.Models.DTOs;
using RestApiTask.Services.Interfaces;
using AutoMapper;

namespace RestApiTask.Controllers;

[ApiController]
[Route("messages")]
public class MessagesController : ControllerBase
{
    private readonly IMessageService _service;
    private readonly IMapper _mapper;

    public MessagesController(IMessageService service, IMapper mapper)
    {
        _service = service;
        _mapper = mapper;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<MessageResponseTo>>> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")]
    public async Task<ActionResult<MessageResponseTo>> GetById(long id) => Ok(await _service.GetByIdAsync(id));

    [HttpPost]
    public async Task<ActionResult<MessageResponseTo>> Create([FromBody] MessageRequestTo request)
    {
        var result = await _service.CreateAsync(request);
        return StatusCode(201, result);
    }

    [HttpPut]
    public async Task<ActionResult<MessageResponseTo>> Update([FromBody] MessageResponseTo responseDto)
    {
        var requestDto = _mapper.Map<MessageRequestTo>(responseDto);
        var result = await _service.UpdateAsync(responseDto.Id, requestDto);
        return Ok(result);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}