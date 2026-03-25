using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    [ApiController]
    [Route("api/v1.0/notes")]
    public class NoteController : ControllerBase
    {
        private readonly INoteService _noteService;
        private readonly IRepository<Issue> _issueRepo;
        private readonly HttpClient _httpClient; 

        public NoteController(INoteService noteService, IRepository<Issue> issueRepo, IHttpClientFactory httpClientFactory)
        {
            _noteService = noteService;
            _issueRepo = issueRepo;
            _httpClient = httpClientFactory.CreateClient("DiscussionClient");
        }

        [HttpPost]
        public IActionResult Create([FromBody] NoteRequestTo request)
        {
            if (_issueRepo.GetById(request.IssueId) == null)
            {
                return BadRequest(new { errorMessage = "Issue not found", errorCode = 40002 });
            }

            if (string.IsNullOrEmpty(request.Content) || request.Content.Length < 2 || request.Content.Length > 2048)
            {
                return BadRequest(new { errorMessage = "Content length error", errorCode = 40008 });
            }

            var result = _noteService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }

        [HttpGet]
        public IActionResult GetAll() => Ok(_noteService.GetAll());

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id)
        {
            var result = _noteService.GetById(id);
            if (result == null) return NotFound();
            return Ok(result);
        }

        [HttpPut("{id:long}")]
        public IActionResult Update(long id, [FromBody] NoteRequestTo request)
        {
            var result = _noteService.Update(id, request);
            if (result == null) return NotFound();
            return Ok(result);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            var response = await _httpClient.DeleteAsync($"notes/{id}");

            if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                return NotFound(new
                {
                    errorMessage = "Note not found",
                    errorCode = 40404
                });
            }

            return NoContent();
        }
    }
}