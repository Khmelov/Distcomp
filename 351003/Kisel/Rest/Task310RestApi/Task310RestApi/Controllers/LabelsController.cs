using Microsoft.AspNetCore.Mvc;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Interfaces;
using Task310RestApi.Exceptions;

namespace Task310RestApi.Controllers
{
    [Route("api/v1.0/labels")]
    [ApiController]
    public class LabelsController : ControllerBase
    {
        private readonly ILabelService _labelService;

        public LabelsController(ILabelService labelService) => _labelService = labelService;

        [HttpGet]
        public async Task<ActionResult<IEnumerable<LabelResponseTo>>> GetLabels() => 
            Ok(await _labelService.GetAllLabelsAsync());

        [HttpGet("{id}")]
        public async Task<ActionResult<LabelResponseTo>> GetLabel(long id) => 
            Ok(await _labelService.GetLabelByIdAsync(id));

        [HttpPost]
        public async Task<ActionResult<LabelResponseTo>> CreateLabel([FromBody] LabelRequestTo labelRequest)
        {
            var createdLabel = await _labelService.CreateLabelAsync(labelRequest);
            return CreatedAtAction(nameof(GetLabel), new { id = createdLabel.Id }, createdLabel);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<LabelResponseTo>> UpdateLabel(long id, [FromBody] LabelRequestTo labelRequest) => 
            Ok(await _labelService.UpdateLabelAsync(id, labelRequest));

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteLabel(long id)
        {
            await _labelService.DeleteLabelAsync(id);
            return NoContent();
        }

        [HttpPut] // Для тестов на ошибку 4xx
        public IActionResult UpdateLabelNoId() => BadRequest(new ErrorResponse("Id is required", "40000"));
    }
}