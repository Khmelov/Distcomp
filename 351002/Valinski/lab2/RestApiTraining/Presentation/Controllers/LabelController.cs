using Application.Abstractions;
using Application.Dtos;
using AutoMapper;
using FluentValidation;
using Microsoft.AspNetCore.Mvc;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Controllers;

[ApiController]
[Route("api/v1.0/labels")]
public class LabelController : ControllerBase
{
    private readonly ILabelService _labelService;
    private readonly IMapper _mapper;
    private readonly IValidator<LabelRequestTo> _labelRequestValidator;
    private readonly IValidator<LabelUpdateRequestTo> _labelUpdateValidator;

    public LabelController(ILabelService labelService, IMapper mapper, IValidator<LabelRequestTo> labelRequestValidator, IValidator<LabelUpdateRequestTo> labelUpdateValidator)
    {
        _labelService = labelService;
        _mapper = mapper;
        _labelRequestValidator = labelRequestValidator;
        _labelUpdateValidator = labelUpdateValidator;
    }

    [HttpGet]
    public async Task<ActionResult<List<LabelResponseTo>>> GetAllLabels()
    {
        var res = _mapper.Map<List<LabelResponseTo>>(await _labelService.GetAllLabelsAsync());
        return Ok(res);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<LabelResponseTo>> GetLabelById(long id)
    {
        var labelFromRepo = await _labelService.GetLabelByIdAsync(id);
        if (labelFromRepo is null)
        {
            return NotFound();
        }

        return Ok(_mapper.Map<LabelResponseTo>(labelFromRepo));
    }

    [HttpPost]
    public async Task<ActionResult<LabelResponseTo>> CreateLabel([FromBody] LabelRequestTo request)
    {
        var validationResult = _labelRequestValidator.Validate(request);
        if (!validationResult.IsValid)
        {
            return BadRequest(new { message = "Validation error" });
        }

        var createDto = _mapper.Map<LabelCreateDto>(request);
        var res = await _labelService.CreateLabelAsync(createDto);
        return CreatedAtAction(nameof(GetLabelById), new { id = res.Id }, _mapper.Map<LabelResponseTo>(res));
    }

    [HttpPut]
    public async Task<ActionResult<LabelResponseTo>> UpdateLabel([FromBody] LabelUpdateRequestTo request)
    {
        var validationResult = _labelUpdateValidator.Validate(request);
        if (!validationResult.IsValid)
        {
            return BadRequest(new { message = "Validation error" });
        }

        var label = await _labelService.UpdateLabelAsync(_mapper.Map<LabelUpdateDto>(request));
        if (label is null)
        {
            return NotFound();
        }

        return Ok(_mapper.Map<LabelResponseTo>(label));
    }

    [HttpDelete("{id:long}")]
    public async Task<ActionResult> DeleteLabel(long id)
    {
        var deletionRes = await _labelService.DeleteLabelAsync(id);
        if (!deletionRes)
        {
            return NotFound();
        }

        return NoContent();
    }
}
