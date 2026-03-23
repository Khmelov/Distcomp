using Microsoft.AspNetCore.Mvc;
using Project.Dto;
using Project.Exceptions;
using Project.Service;

namespace Project.Controller {
    [ApiController]
    [Route("api/v1.0/stickers")]
    public class StickerController : BaseController<StickerRequestTo, StickerResponseTo> {
        private readonly StickerService _stickerService;

        public StickerController(StickerService stickerService, ILogger<StickerController> logger)
            : base(logger) {
            _stickerService = stickerService;
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<StickerResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<StickerResponseTo>>> GetStickers() {
            var stickers = await _stickerService.GetAllAsync();
            return Ok(stickers);
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(StickerResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<StickerResponseTo>> GetSticker(long id) {
            var sticker = await _stickerService.GetByIdAsync(id);
            return sticker == null ? NotFound() : Ok(sticker);
        }

        [HttpPost]
        [ProducesResponseType(typeof(StickerResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<StickerResponseTo>> CreateSticker([FromBody] StickerRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var sticker = await _stickerService.AddAsync(request);
            return CreatedAtAction(nameof(GetSticker), new { id = sticker.Id }, sticker);
        }

        [HttpPut]
        [ProducesResponseType(typeof(StickerResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<StickerResponseTo>> UpdateSticker([FromBody] StickerRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var updatedSticker = await _stickerService.UpdateAsync(request);
            return updatedSticker == null ? NotFound() : Ok(updatedSticker);
        }


        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(StickerResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<StickerResponseTo>> UpdateSticker(
            long id, [FromBody] StickerRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            request.Id = id;

            try {
                var updatedSticker = await _stickerService.UpdateAsync(request);
            return updatedSticker == null ? NotFound() : Ok(updatedSticker);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DeleteSticker(long id) {
            var deleted = await _stickerService.DeleteAsync(id);
            return deleted ? NoContent() : NotFound();
        }
    }
}