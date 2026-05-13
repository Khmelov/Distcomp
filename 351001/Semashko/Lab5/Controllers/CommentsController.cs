using System.Diagnostics;
using System.Net;
using Confluent.Kafka;
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
    [Route("api/v1.0/comments")]
    public class CommentsController : ControllerBase
    {
        private IAsyncService<CommentRequestDto,CommentResponseDto> authorService;
        private IValidator<CommentRequestDto> _authorValidator;
        private IDistributedCache _redis;
        public CommentsController(IAsyncService<CommentRequestDto,CommentResponseDto> authorService, IValidator<CommentRequestDto> authorValidator,IDistributedCache redis)
        {
            this.authorService = authorService;
            _authorValidator = authorValidator;
            _redis = redis;
        }
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<CommentResponseDto>> GetAuthors() => Ok(authorService.GetAllAsync());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public async Task<ActionResult<CommentResponseDto>> CreateAuthor([FromBody]CommentRequestDto dto)  {
            try
            {
                if (_authorValidator.Validate(dto).IsValid)
                    return CreatedAtAction("CreateAuthor", await authorService.CreateAsync(dto));
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
           return authorService.DeleteAsync(id).IsCompleted?NoContent():NotFound(); 
        }

        [HttpPut]
        public ActionResult<CommentResponseDto> UpdateAuthor([FromBody] CommentRequestDto dto)
        {

            return authorService.UpdateAsync(dto) == null ? NotFound(dto) : Ok(dto);
        }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async  Task<ActionResult<AuthorResponseDto>> GetAuthor(long id)
        {
            var res = JsonConvert.DeserializeObject(await _redis.GetStringAsync($"markers/{id}"));
            if (res == null)
            {
                res = await authorService.ReadAsync(id);
                await _redis.SetStringAsync($"markers/{id}", JsonConvert.SerializeObject(res));
            }
            return Ok(res);
        }
        
    }
}
