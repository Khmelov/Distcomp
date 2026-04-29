using Microsoft.AspNetCore.Mvc;
using RestApiTask.Models.DTOs;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;
using AutoMapper;

namespace RestApiTask.Controllers;

[ApiController]
[Route("articles")]
public class ArticlesController : ControllerBase
{
    private readonly IArticleService _service;
    private readonly IMapper _mapper;

    public ArticlesController(IArticleService service, IMapper mapper)
    {
        _service = service;
        _mapper = mapper;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ArticleResponseTo>>> GetAll([FromQuery] QueryOptions? options) =>
        Ok(await _service.GetAllAsync(options));

    [HttpGet("{id}")]
    public async Task<ActionResult<ArticleResponseTo>> GetById(long id) => Ok(await _service.GetByIdAsync(id));

    [HttpPost]
    public async Task<ActionResult<ArticleResponseTo>> Create([FromBody] ArticleRequestTo request)
    {
        var result = await _service.CreateAsync(request);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<ActionResult<ArticleResponseTo>> Update([FromBody] ArticleResponseTo responseDto)
    {
        var requestDto = _mapper.Map<ArticleRequestTo>(responseDto);
        var result = await _service.UpdateAsync(responseDto.Id, requestDto);
        return Ok(result);
    }

    [HttpPut("{id}")]
    public async Task<ActionResult<ArticleResponseTo>> Update(long id, [FromBody] ArticleRequestTo requestDto)
    {
        var result = await _service.UpdateAsync(id, requestDto);
        return Ok(result);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }

}