using Asp.Versioning;
using DistComp.Models;
using DistComp.Services.Implementation;
using Microsoft.AspNetCore.Mvc;

namespace DistComp.Controllers {
    [Route("api/v1.0/topics")]
    [ApiController]
    [ApiVersion("1.0")]
    public class TopicController : ControllerBase {
        private readonly TopicService topicService;
        private readonly ILogger<TopicService> logger;

        public TopicController(TopicService service, ILogger<TopicService> logger) {
            this.topicService = service;
            this.logger = logger;
        }

        // GET *
        [HttpGet]
        public IEnumerable<TopicOutDto> GetAll() {
            return topicService.GetAll();
        }

        // GET */5
        [HttpGet("{id}")]
        public ActionResult GetById(int id) {
            var Topic = topicService.Get(id);
            if (Topic == null) {
                return NotFound();
            } else {
                return Ok(Topic);
            }
        }

        // POST *
        [HttpPost]
        public ActionResult Create([FromBody] TopicInDto newTopic) {
            try {
                var Topic = topicService.Create(newTopic);
                return StatusCode(StatusCodes.Status201Created, Topic);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status403Forbidden);
            }
        }

        // PUT *
        [HttpPut]
        public ActionResult Update([FromBody] Topic data) {
            try {
                var Topic = topicService.Update(data);
                return Ok(Topic);
            } catch (Exception ex) {
                return NotFound();
            }
        }

        // DELETE */5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id) {
            try {
                topicService.Delete(id);
                return StatusCode(StatusCodes.Status204NoContent);
            } catch (Exception ex) {
                return NotFound();
            }
        }
    }
}
