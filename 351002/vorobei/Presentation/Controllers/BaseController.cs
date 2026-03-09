using Microsoft.AspNetCore.Mvc;
using BusinessLogic.Servicies;

using DataAccess.Models;

namespace Presentation.Controllers
{
    [Route("api/v1.0/[controller]")]
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
        public virtual ActionResult<List<TResponse>> GetAll()
        {
            return Ok(_service.GetAll());
        }

        [HttpGet("{id}")]
        public virtual ActionResult<TResponse> GetById(int id)
        {
            TResponse? response = _service.GetById(id);
            if (response != null)
            {
                return Ok(response);
            }
            return NotFound();
        }

        [HttpPost]
        public virtual ActionResult<TResponse> Create([FromBody] TRequest entity)
        {
            TResponse response = _service.Create(entity);
            return Created($"{response.Id}", response);
        }

        [HttpPut]
        public virtual ActionResult<TResponse> Update([FromBody] TRequest entity)
        {
            TResponse? response = _service.Update(entity);
            if (response != null)
            {
                return Ok(response);
            }
            return NotFound();
        }

        [HttpDelete("{id}")]
        public virtual ActionResult Delete(int id)
        {
            bool wasFound = _service.DeleteById(id);
            if (wasFound)
            {
                return NoContent();
            }
            return NotFound();
        }
    }
}