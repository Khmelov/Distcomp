using distcomp.DTOs;
using distcomp.Services;
using Microsoft.AspNetCore.Mvc;

namespace distcomp.Controllers
{
    [ApiController]
    [Route("api/v1.0/creators")]
    public class CreatorController : ControllerBase
    {
        private readonly CreatorService _creatorService;

        public CreatorController(CreatorService creatorService)
        {
            _creatorService = creatorService;
        }

        
        [HttpPost]
        public IActionResult CreateCreator([FromBody] CreatorRequestTo creatorRequest)
        {
            var creatorResponse = _creatorService.CreateCreator(creatorRequest);
            return CreatedAtAction(nameof(GetCreator), new { id = creatorResponse.Id }, creatorResponse);
        }

        
        [HttpGet("{id}")]
        public IActionResult GetCreator(long id)
        {
            var creatorResponse = _creatorService.GetCreatorById(id);
            return creatorResponse != null ? Ok(creatorResponse) : NotFound();
        }

        
        [HttpGet]
        public IActionResult GetAllCreators()
        {
            var creatorResponses = _creatorService.GetAllCreators();
            return Ok(creatorResponses);
        }

        
        [HttpPut]/////
        public IActionResult UpdateCreator(long id, [FromBody] CreatorRequestTo creatorRequest)
        {
            //var updatedUser = _userService.UpdateUser(id, userRequest);
            // return userResponse != null ? Ok(userResponse) : NotFound();

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (creatorRequest == null || creatorRequest.Id <= 0)
            {
                return BadRequest("Invalid user data.");
            }

            var updatedCreator = _creatorService.UpdateCreator(creatorRequest.Id, creatorRequest);
            if (updatedCreator == null)
            {
                return NotFound();
            }
            return Ok(updatedCreator);
        }

        
        [HttpDelete("{id}")]
        public IActionResult DeleteCreator(long id)
        {
            var isDeleted = _creatorService.DeleteCreator(id);
            return isDeleted ? NoContent() : NotFound();
        }
    }
}
