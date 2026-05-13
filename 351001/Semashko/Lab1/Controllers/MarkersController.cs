using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/markers")]
    public class MarkersController : ControllerBase
    {
        private readonly BaseService<MarkerRequestDto,MarkerResponseDto> _service;
        public MarkersController(BaseService<MarkerRequestDto,MarkerResponseDto> markerService)
        {
            _service = markerService;
        }

        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<MarkerResponseDto>> GetMarkers() => Ok(_service.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<MarkerResponseDto> CreateMarker([FromBody]MarkerRequestDto dto)
        {
            var created = _service.Create(dto);
            return CreatedAtAction(nameof(GetMarker), new { id = created?.id }, created);
        }

        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public ActionResult DeleteMarker(long id)
        {
            return _service.Delete(id) ? NoContent() : NotFound();
        }

        [HttpPut]
        public ActionResult<MarkerResponseDto> UpdateMarker([FromBody] MarkerRequestDto dto)
        {
            var updated = _service.Update(dto);
            return updated == null ? NotFound() : Ok(updated);
        }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<MarkerResponseDto> GetMarker(long id)
        {
            var result = _service.Read(id);
            return result == null ? NotFound() : Ok(result);
        }
    }
}
