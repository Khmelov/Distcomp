using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    public class IssueController : BaseController
    {
        private readonly IIssueService _issueService;

        public IssueController(IIssueService issueService) => _issueService = issueService;

        [HttpPost]
        public IActionResult Create([FromBody] IssueRequestTo request)
        {
            var response = _issueService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id) => Ok(_issueService.GetById(id));

        [HttpGet]
        public IActionResult GetAll() => Ok(_issueService.GetAll());

        [HttpPut("{id:long?}")]
        public IActionResult Update(long id, [FromBody] IssueRequestTo request) =>
            Ok(_issueService.Update(id, request));

        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            _issueService.Delete(id);
            return NoContent();
        }
    }
}