using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    public class NoteController : BaseController
    {
        private readonly INoteService _noteService;

        public NoteController(INoteService noteService)
        {
            _noteService = noteService;
        }

        [HttpPost]
        public IActionResult Create([FromBody] NoteRequestTo request)
        {
            var response = _noteService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id)
        {
            var response = _noteService.GetById(id);
            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_noteService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public IActionResult Update(long id, [FromBody] NoteRequestTo request)
        {
            var response = _noteService.Update(id, request);
            return Ok(response);
        }

        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            _noteService.Delete(id);
            return NoContent();
        }
    }
}