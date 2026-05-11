using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/markers")]
    public class MarkersController : ControllerBase
    {
        private IBaseService<MarkerRequestDto,MarkerResponseDto> authorService;
        private IValidator<MarkerRequestDto> _authorValidator;
        public MarkersController(IBaseService<MarkerRequestDto,MarkerResponseDto> authorService,IValidator<MarkerRequestDto> authorValidator)
        {
            this.authorService = authorService;
            _authorValidator = authorValidator;
        }
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<MarkerResponseDto>> GetAuthors() => Ok(authorService.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<MarkerResponseDto> CreateAuthor([FromBody]MarkerRequestDto dto)  { try
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
        public ActionResult<MarkerResponseDto> UpdateAuthor([FromBody] MarkerRequestDto dto)
        {

            return authorService.Update(dto) == null ? NotFound(dto) : Ok(dto);
        }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<CommentResponseDto> GetAuthor(long id) => Ok(authorService.Read(id));

    }
}
