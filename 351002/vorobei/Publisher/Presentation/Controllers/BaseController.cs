using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Infrastructure.Exceptions;
using Microsoft.AspNetCore.Mvc;

namespace Presentation.Controllers
{
    [ApiController]
    public abstract class BaseController<TEntity, TRequest, TResponse> : ControllerBase
        where TEntity : BaseEntity
        where TRequest : class
        where TResponse : BaseEntity
    {
        protected readonly IBaseService<TRequest, TResponse> _service;

        protected BaseController(IBaseService<TRequest, TResponse> service)
        {
            _service = service;
        }

        [HttpGet]
        public async virtual Task<ActionResult<List<TResponse>>> GetAllAsync()
        {
            return Ok(await _service.GetAllAsync());
        }

        [HttpGet("{id}")]
        public async virtual Task<ActionResult<TResponse>> GetByIdAsync(int id)
        {
            TResponse? response = await _service.GetByIdAsync(id);
            if (response != null)
            {
                return Ok(response);
            }
            return NotFound();
        }

        [HttpPost]
        public async virtual Task<ActionResult<TResponse>> CreateAsync([FromBody] TRequest entity)
        {
            try
            {
                TResponse? response = await _service.CreateAsync(entity);
                return Created($"{response.Id}", response);
            }
            catch (BaseException ex)
            {
                return StatusCode(ex.StatusCode, new { error = ex.Message });
            }
        }

        [HttpPut]
        public async virtual Task<ActionResult<TResponse>> UpdateAsync([FromBody] TRequest entity)
        {
            TResponse? response = await _service.UpdateAsync(entity);
            if (response != null)
            {
                return Ok(response);
            }
            return NotFound();
        }

        [HttpDelete("{id}")]
        public async virtual Task<ActionResult> Delete(int id)
        {
            bool wasFound = await _service.DeleteByIdAsync(id);
            if (wasFound)
            {
                return NoContent();
            }
            return NotFound();
        }
    }
}