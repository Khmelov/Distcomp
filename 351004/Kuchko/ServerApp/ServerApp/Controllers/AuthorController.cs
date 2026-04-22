using Microsoft.AspNetCore.Mvc;
using ServerApp.Models.DTOs;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Services.Interfaces;

namespace ServerApp.Controllers;

[ApiController]
[Route("authors")] // Итоговый путь: /api/v1.0/authors
public class AuthorController(IAuthorService authorService) : ControllerBase
{
    [HttpGet]
    public ActionResult<IEnumerable<AuthorResponseTo>> GetAll()
    {
        return Ok(authorService.GetAll());
    }

    [HttpGet("{id:long}")]
    public ActionResult<AuthorResponseTo> GetById(long id)
    {
        return Ok(authorService.GetById(id));
    }

    [HttpPost]
    public ActionResult<AuthorResponseTo> Create([FromBody] AuthorRequestTo request)
    {
        var response = authorService.Create(request);
        // Возвращаем статус 201 Created и ссылку на получение созданного объекта
        return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
    }

    [HttpPut("{id:long}")] // Поддержка /api/v1.0/authors/{id}
    [HttpPut]              // Поддержка /api/v1.0/authors (ID внутри JSON)
    public ActionResult<AuthorResponseTo> Update(long? id, [FromBody] AuthorRequestTo request)
    {
        long finalId = id ?? (request.Id ?? 0);

        if (finalId == 0)
        {
            return BadRequest(new ErrorResponse("ID must be provided in URL or body", 40002));
        }

        // Вызываем сервис с найденным ID
        return Ok(authorService.Update(finalId, request));
    }

    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        authorService.Delete(id);
        return NoContent(); // Статус 204 No Content по ТЗ
    }
}