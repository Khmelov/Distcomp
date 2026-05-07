using Application.Abstractions;
using Application.Dtos;
using AutoMapper;
using FluentValidation;
using Microsoft.AspNetCore.Mvc;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Controllers;

[ApiController]
[Route("api/v1.0/reactions")]
public class ReactionController : ControllerBase
{
    private readonly IReactionService _reactionService;
    private readonly IMapper _mapper;
    private readonly IValidator<ReactionRequestTo> _reactionRequestValidator;
    private readonly IValidator<ReactionUpdateRequestTo> _reactionUpdateValidator;

    public ReactionController(IReactionService reactionService, IMapper mapper, IValidator<ReactionRequestTo> reactionRequestValidator, IValidator<ReactionUpdateRequestTo> reactionUpdateValidator)
    {
        _reactionService = reactionService;
        _mapper = mapper;
        _reactionRequestValidator = reactionRequestValidator;
        _reactionUpdateValidator = reactionUpdateValidator;
    }

    [HttpGet]
    public async Task<ActionResult<List<ReactionResponseTo>>> GetAllReactions()
    {
        var res = _mapper.Map<List<ReactionResponseTo>>(await _reactionService.GetAllReactionsAsync());
        return Ok(res);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<ReactionResponseTo>> GetReactionById(long id)
    {
        var reactionFromRepo = await _reactionService.GetReactionByIdAsync(id);
        return Ok(_mapper.Map<ReactionResponseTo>(reactionFromRepo));
    }

    [HttpPost]
    public async Task<ActionResult<ReactionResponseTo>> CreateReaction([FromBody] ReactionRequestTo request)
    {
        var validationResult = _reactionRequestValidator.Validate(request);
        if (!validationResult.IsValid)
        {
            return BadRequest(new { message = "Validation error" });
        }

        var createDto = _mapper.Map<ReactionCreateDto>(request);
        var res = await _reactionService.CreateReactionAsync(createDto);
        return CreatedAtAction(nameof(GetReactionById), new { id = res.Id }, _mapper.Map<ReactionResponseTo>(res));
    }

    [HttpPut]
    public async Task<ActionResult<ReactionResponseTo>> UpdateReaction([FromBody] ReactionUpdateRequestTo request)
    {
        var validationResult = _reactionUpdateValidator.Validate(request);
        if (!validationResult.IsValid)
        {
            return BadRequest(new { message = "Validation error" });
        }

        var reaction = await _reactionService.UpdateReactionAsync(_mapper.Map<ReactionUpdateDto>(request));
        return Ok(_mapper.Map<ReactionResponseTo>(reaction));
    }

    [HttpDelete("{id:long}")]
    public async Task<ActionResult> DeleteReaction(long id)
    {
        var deletionRes = await _reactionService.DeleteReactionAsync(id);
        if (!deletionRes)
        {
            return NotFound();
        }

        return NoContent();
    }
}
