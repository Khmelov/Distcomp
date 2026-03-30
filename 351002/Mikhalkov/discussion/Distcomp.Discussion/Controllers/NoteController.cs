using Distcomp.Discussion.Domain.Models;
using Distcomp.Discussion.Infrastructure.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.Discussion.Controllers
{
    [ApiController]
    [Route("api/v1.0/notes")]
    public class NoteController : ControllerBase
    {
        private readonly NoteRepository _repository;

        public NoteController(NoteRepository repository)
        {
            _repository = repository;
        }

        [HttpPost]
        public IActionResult Create([FromBody] Note note)
        {
            if (note.Id == 0) note.Id = DateTime.UtcNow.Ticks;

            _repository.Save(note);
            return Created("", note);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            var notes = _repository.GetAll();
            return Ok(notes);
        }

        [HttpGet("{country}/{issueId}/{id}")]
        public IActionResult GetById(string country, long issueId, long id)
        {
            var note = _repository.GetOne(country, issueId, id);
            return note == null ? NotFound() : Ok(note);
        }

        [HttpPut("{id:long}")]
        public IActionResult Update(long id, [FromBody] Note note)
        {
            note.Id = id;
            _repository.Save(note);
            return Ok(note);
        }

        [HttpDelete("{country}/{issueId}/{id:long}")]
        public IActionResult Delete(string country, long issueId, long id)
        {
            if (!_repository.Exists(country, issueId, id)) return NotFound();
            _repository.Delete(country, issueId, id);
            return NoContent();
        }

        [HttpDelete("{id:long}")]
        public IActionResult DeleteById(long id)
        {
            var note = _repository.GetAll().FirstOrDefault(n => n.Id == id);

            if (note == null) return NotFound();

            _repository.Delete(note.Country, note.IssueId, note.Id);
            return NoContent();
        }

        [HttpGet("{id:long}")]
        public IActionResult GetByIdSimple(long id)
        {
            var note = _repository.GetAll().FirstOrDefault(n => n.Id == id);

            if (note == null) return NotFound();

            return Ok(note);
        }
    }
}