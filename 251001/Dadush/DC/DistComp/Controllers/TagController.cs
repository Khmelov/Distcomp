using Asp.Versioning;
using DistComp.Models;
using DistComp.Services.Implementation;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DistComp.Controllers {
    [Route("api/v1.0/tags")]
    [ApiController]
    [ApiVersion("1.0")]
    public class TagController : ControllerBase {
        private readonly TagService tagService;
        private readonly ILogger<TagService> logger;

        public TagController(TagService service, ILogger<TagService> logger) {
            this.tagService = service;
            this.logger = logger;
        }

        // GET *
        [HttpGet]
        public IEnumerable<TagOutDto> GetAll() {
            return tagService.GetAll();
        }

        // GET */5
        [HttpGet("{id}")]
        public ActionResult GetById(int id) {
            var Tag = tagService.Get(id);
            if (Tag == null) {
                return NotFound();
            } else {
                return Ok(Tag);
            }
        }

        // POST *
        [HttpPost]
        public ActionResult Create([FromBody] TagInDto newTag) {
            try {
                var Tag = tagService.Create(newTag);
                return StatusCode(StatusCodes.Status201Created, Tag);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status403Forbidden);
            }
        }

        // PUT *
        [HttpPut]
        public ActionResult Update([FromBody] Tag data) {
            try {
                var Tag = tagService.Update(data);
                return Ok(Tag);
            } catch (Exception ex) {
                return NotFound();
            }
        }

        // DELETE */5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id) {
            try {
                tagService.Delete(id);
                return StatusCode(StatusCodes.Status204NoContent);
            } catch (InvalidOperationException ex) {
                return BadRequest();
            } catch (Exception ex) {
                return NotFound();
            }
        }
    }
}
