using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    public class MarkerController : BaseController
    {
        private readonly IMarkerService _markerService;

        public MarkerController(IMarkerService markerService)
        {
            _markerService = markerService;
        }

        [HttpPost]
        public IActionResult Create([FromBody] MarkerRequestTo request)
        {
            var response = _markerService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id)
        {
            var response = _markerService.GetById(id);
            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_markerService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public IActionResult Update(long id, [FromBody] MarkerRequestTo request)
        {
            var response = _markerService.Update(id, request);
            return Ok(response);
        }

        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            _markerService.Delete(id);
            return NoContent();
        }
    }
}