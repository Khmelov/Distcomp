using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers;

[ApiController]
[Route("api/v2.0/markers")]
[Authorize]
public class MarkersV2Controller : ControllerBase
{
    private readonly IBaseService<MarkerRequestDto, MarkerResponseDto> _markerService;
    private readonly IValidator<MarkerRequestDto> _markerValidator;

    public MarkersV2Controller(
        IBaseService<MarkerRequestDto, MarkerResponseDto> markerService,
        IValidator<MarkerRequestDto> markerValidator)
    {
        _markerService = markerService;
        _markerValidator = markerValidator;
    }

    [HttpGet]
    public ActionResult<IEnumerable<MarkerResponseDto>> GetMarkers() => Ok(_markerService.GetAll());

    [HttpGet("{id:long}")]
    public ActionResult<MarkerResponseDto> GetMarker(long id)
    {
        var marker = _markerService.Read(id);
        if (marker == null)
        {
            return NotFound(new ErrorResponseDto
            {
                ErrorMessage = "Marker not found.",
                ErrorCode = "40404"
            });
        }

        return Ok(marker);
    }

    [HttpPost]
    [Authorize(Roles = "ADMIN")]
    public ActionResult<MarkerResponseDto> CreateMarker([FromBody] MarkerRequestDto dto)
    {
        var validation = _markerValidator.Validate(dto);
        if (!validation.IsValid)
        {
            return BadRequest(new ErrorResponseDto
            {
                ErrorMessage = validation.Errors.First().ErrorMessage,
                ErrorCode = "40003"
            });
        }

        var created = _markerService.Create(dto);
        return CreatedAtAction(nameof(GetMarker), new { id = created?.Id }, created);
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]
    public ActionResult<MarkerResponseDto> UpdateMarker([FromBody] MarkerRequestDto dto) => Ok(_markerService.Update(dto));

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public ActionResult DeleteMarker(long id) => _markerService.Delete(id) ? NoContent() : NotFound(new ErrorResponseDto
    {
        ErrorMessage = "Marker not found.",
        ErrorCode = "40405"
    });
}
