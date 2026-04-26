using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using Distcomp.Infrastructure.Caching;
using Microsoft.AspNetCore.Mvc;

namespace Distcomp.WebApi.Controllers
{
    public class UserController : BaseController
    {
        private readonly IUserService _userService;
        private readonly RedisCacheService _cache; 

        public UserController(IUserService userService, RedisCacheService cache)
        {
            _userService = userService;
            _cache = cache; 
        }

        [HttpPost]
        public IActionResult Create([FromBody] UserRequestTo request)
        {
            var response = _userService.Create(request);
            return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
        }

        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetById(long id) 
        {
            string cacheKey = $"user:{id}";

            var cachedUser = await _cache.GetAsync(cacheKey);
            if (!string.IsNullOrEmpty(cachedUser))
            {
                Console.WriteLine($"[Redis] User {id} found in cache");
                return Content(cachedUser, "application/json");
            }

            var response = _userService.GetById(id);
            if (response == null) return NotFound();

            await _cache.SetAsync(cacheKey, response);

            return Ok(response);
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_userService.GetAll());
        }

        [HttpPut("{id:long?}")]
        public async Task<IActionResult> Update(long id, [FromBody] UserRequestTo request)
        {
            await _cache.RemoveAsync($"user:{id}");

            var response = _userService.Update(id, request);
            return Ok(response);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            await _cache.RemoveAsync($"user:{id}");

            _userService.Delete(id);
            return NoContent();
        }
    }
}