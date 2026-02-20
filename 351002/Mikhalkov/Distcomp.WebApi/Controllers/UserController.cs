using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    public class UserController : BaseController
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost]
        public IActionResult Create([FromBody] UserRequestTo request)
        {
            var response = _userService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public IActionResult GetById(long id)
        {
            var response = _userService.GetById(id);
            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_userService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public IActionResult Update(long id, [FromBody] UserRequestTo request)
        {
            var response = _userService.Update(id, request);
            return Ok(response);
        }

        [HttpDelete("{id:long}")]
        public IActionResult Delete(long id)
        {
            _userService.Delete(id);
            return NoContent();
        }
    }
}