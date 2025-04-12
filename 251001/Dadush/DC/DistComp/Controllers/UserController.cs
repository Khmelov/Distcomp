using Asp.Versioning;
using DistComp.Models;
using DistComp.Services.Implementation;
using Microsoft.AspNetCore.Mvc;

namespace DistComp.Controllers {

    [Route("api/v1.0/users")]
    [ApiController]
    [ApiVersion("1.0")]
    public class UserController : ControllerBase {

        private readonly UserService userService;
        private readonly ILogger<UserService> logger;

        public UserController(UserService service, ILogger<UserService> logger) {
            this.userService = service;
            this.logger = logger;
        }

        // GET *
        [HttpGet]
        public async Task<IEnumerable<UserOutDto>> GetAll() {
            return await userService.GetAll();
        }

        // GET */5
        [HttpGet("{id}")]
        public async Task<ActionResult> GetById(int id) {
            var user = await userService.Get(id);
            if (user == null) {
                return NotFound();
            } else {
                return Ok(user);
            }
        }

        // POST *
        [HttpPost]
        public async Task<ActionResult> Create([FromBody] UserInDto newUser) {
            try {
                var user = await userService.Create(newUser);
                return StatusCode(StatusCodes.Status201Created, user);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status403Forbidden);
            }
        }

        // PUT *
        [HttpPut]
        public async Task<ActionResult> Update([FromBody] User data) {
            try {
                var user = await userService.Update(data);
                return Ok(user);
            } catch (Exception ex) {
                return NotFound();
            }
        }

        // DELETE */5
        [HttpDelete("{id}")]
        public async Task<ActionResult> Delete(int id) {
            try {
                await userService.Delete(id);
                return StatusCode(StatusCodes.Status204NoContent);
            } catch (Exception ex) {
                return NotFound();
            }
        }
    }
}
