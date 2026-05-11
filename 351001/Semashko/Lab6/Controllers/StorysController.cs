using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Newtonsoft.Json;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/storys")]
    public class StorysController : ControllerBase
    {
        private IBaseService<StoryRequestDto,StoryResponseDto> _authorService;
        private IValidator<StoryRequestDto> _authorValidator;
        private IDistributedCache _redis;
        
        public StorysController(IBaseService<StoryRequestDto,StoryResponseDto> authorService, IValidator<StoryRequestDto> authorValidator, IDistributedCache redis)
        {
            _authorService = authorService;
            _authorValidator = authorValidator;
            _redis = redis;
        }
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<StoryResponseDto>> GetAuthors() => Ok(_authorService.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<StoryResponseDto> CreateAuthor([FromBody] StoryRequestDto dto)
        {
            try
            {
                if (_authorValidator.Validate(dto).IsValid)
                    return CreatedAtAction("CreateAuthor", _authorService.Create(dto));
            }
            catch (DbUpdateException e)
            {                   
                return StatusCode(StatusCodes.Status403Forbidden);
            }
            return BadRequest();
        }

        [HttpDelete("{id}")]

        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public ActionResult DeleteAuthor(long id)
        {
            
            return _authorService.Delete(id)?NoContent():NotFound(); 
        }

        [HttpPut]
        public ActionResult<StoryResponseDto> UpdateAuthor([FromBody] StoryRequestDto dto)
        {

            return _authorService.Update(dto) == null ? NotFound(dto) : Ok(dto);
        }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async  Task<ActionResult<AuthorResponseDto>> GetAuthor(long id)
        {
            var res = JsonConvert.DeserializeObject(await _redis.GetStringAsync($"markers/{id}"));
            if (res == null)
            {
                res = _authorService.Read(id);
                await _redis.SetStringAsync($"markers/{id}", JsonConvert.SerializeObject(res));
            }
            return Ok(res);
        }
    }
}
