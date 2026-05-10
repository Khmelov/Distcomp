using Application.Abstractions;
using Application.Dtos;
using AutoMapper;
using FluentValidation;
using Microsoft.AspNetCore.Mvc;
using Presentation.Contracts.Requests;
using Presentation.Contracts.Responses;

namespace Presentation.Controllers;

[ApiController]
[Route("api/v1.0/topics")]
public class TopicController : ControllerBase
{
    private readonly ITopicService _topicService;
    private readonly IMapper _mapper;
    private readonly IValidator<TopicRequestTo> _topicRequestValidator;
    private readonly IValidator<TopicUpdateRequestTo> _topicUpdateValidator;
    
    public TopicController(ITopicService topicService, IMapper mapper, IValidator<TopicRequestTo> topicRequestValidator, IValidator<TopicUpdateRequestTo> topicUpdateValidator)
    {
        _topicService = topicService;
        _mapper = mapper;
        _topicRequestValidator = topicRequestValidator;
        _topicUpdateValidator = topicUpdateValidator;
    }

    [HttpGet]
    public async Task<ActionResult<List<TopicResponseTo>>> GetAllTopics()
    {
        var res = _mapper.Map<List<TopicResponseTo>>(await _topicService.GetAllTopicsAsync());
        return Ok(res);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<TopicResponseTo>> GetTopicById(long id)
    {
        var topicFromRepo = await _topicService.GetTopicByIdAsync(id);
        return Ok(_mapper.Map<TopicResponseTo>(topicFromRepo));
    }
    
    [HttpPost]
    public async Task<ActionResult<TopicResponseTo>> CreateTopic([FromBody] TopicRequestTo request)
    {
        var validationResult = _topicRequestValidator.Validate(request);

        if (!validationResult.IsValid)
        {
            return BadRequest(new { message = "Validation error" });
        }
        
        var createDto = _mapper.Map<TopicCreateDto>(request); 
        
        var res = await _topicService.CreateTopicAsync(createDto);
        return CreatedAtAction(nameof(GetTopicById), new {id = res.Id}, res);
    }

    [HttpPut]
    public async Task<ActionResult<TopicResponseTo>> UpdateTopic([FromBody]TopicUpdateRequestTo request)
    {
        var validationResult = _topicUpdateValidator.Validate(request);
        if (!validationResult.IsValid)
        {
            return BadRequest(new { message = "Validation error" });
        }
        
        var topic = await _topicService.UpdateTopicAsync(_mapper.Map<TopicUpdateDto>(request));

        if (topic is null)
        {
            return NotFound();
        }

        return _mapper.Map<TopicResponseTo>(topic);
    }

    [HttpDelete("{id:long}")]
    public async Task<ActionResult> DeleteTopic(long id)
    {
        var deletionRes = await _topicService.DeleteTopicAsync(id);
        if (!deletionRes)
        {
            return NotFound();
        }
        
        return NoContent();
    }
    
}
