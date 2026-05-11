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
        private IBaseService<CommentRequestDto,CommentResponseDto> authorService;
        private IValidator<CommentRequestDto> _authorValidator;
        public CommentsController(IBaseService<CommentRequestDto,CommentResponseDto> authorService, IValidator<CommentRequestDto> authorValidator)
        {
            this.authorService = authorService;
            _authorValidator = authorValidator;
            
        }
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<CommentResponseDto>> GetAuthors() => Ok(authorService.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<CommentResponseDto> CreateAuthor([FromBody]CommentRequestDto dto)  {
            try
            {
                if (_authorValidator.Validate(dto).IsValid)
                    return CreatedAtAction("CreateAuthor", authorService.Create(dto));
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
            return authorService.Delete(id)?NoContent():NotFound(); 
        }

        [HttpPut]
        public ActionResult<CommentResponseDto> UpdateAuthor([FromBody] CommentRequestDto dto)
        {

            return authorService.Update(dto) == null ? NotFound(dto) : Ok(dto);
            }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<CommentResponseDto> GetAuthor(long id) => Ok(authorService.Read(id));

    }
}
