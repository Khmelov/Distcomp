using FluentValidation;
using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Newtonsoft.Json;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/authors")]
    public class AuthorsController:ControllerBase
    {
        private IBaseService<AuthorRequestDto,AuthorResponseDto> _authorService;
        private IValidator<AuthorRequestDto> _authorValidator;
        private IDistributedCache _redis;
        
        public AuthorsController(IBaseService<AuthorRequestDto,AuthorResponseDto> authorService, IValidator<AuthorRequestDto> authorValidator,IDistributedCache redis)
        {
            _authorService = authorService;
            _authorValidator = authorValidator;
            _redis = redis;
        }
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<AuthorResponseDto>> GetAuthors() => Ok(_authorService.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<AuthorResponseDto> CreateAuthor([FromBody] AuthorRequestDto dto)
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
        public ActionResult<AuthorResponseDto> UpdateAuthor([FromBody] AuthorRequestDto dto)
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
