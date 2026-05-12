using System.Diagnostics;
using System.Net;
using Confluent.Kafka;
using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/comments")]
    public class CommentsController : ControllerBase
    {
        private IAsyncService<CommentRequestDto,CommentResponseDto> authorService;
        private IValidator<CommentRequestDto> _authorValidator;
        public CommentsController(IAsyncService<CommentRequestDto,CommentResponseDto> authorService, IValidator<CommentRequestDto> authorValidator)
        {
            this.authorService = authorService;
            _authorValidator = authorValidator;
            
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
        public ActionResult<CommentResponseDto> GetAuthor(long id) => Ok(authorService.ReadAsync(id).Result);
        
    }
}
