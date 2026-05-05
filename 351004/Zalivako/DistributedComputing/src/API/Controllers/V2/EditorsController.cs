using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace API.Controllers.V2
{
    [ApiController]
    [Route("api/v2.0/[controller]")]
    public class EditorsController : ControllerBase
    {
        private readonly IEditorService _editorService;
        private readonly ILogger<EditorsController> _logger;

        public EditorsController(IEditorService editorService, ILogger<EditorsController> logger)
        {
            _editorService = editorService;
            _logger = logger;
        }

        // POST – регистрация (открыт)
        [HttpPost]
        [AllowAnonymous]
        public async Task<ActionResult<EditorResponseTo>> CreateEditor([FromBody] EditorRequestTo request)
        {
            var response = await _editorService.CreateEditor(request);
            return CreatedAtAction(nameof(GetEditorById), new { id = response.Id }, response);
        }

        // GET – список редакторов (доступно всем аутентифицированным)
        [HttpGet]
        [Authorize(Roles = "ADMIN,CUSTOMER")]
        public async Task<ActionResult<IEnumerable<EditorResponseTo>>> GetAllEditors()
        {
            var editors = await _editorService.GetAllEditors();
            return Ok(editors);
        }

        // GET по id – свой профиль доступен полностью, чужой только чтение
        [HttpGet("{id:long}")]
        [Authorize(Roles = "ADMIN,CUSTOMER")]
        public async Task<ActionResult<EditorResponseTo>> GetEditorById(long id)
        {
            var editor = await _editorService.GetEditor(new EditorRequestTo { Id = id });
            return Ok(editor);
        }

        // PUT – обновление профиля: только свой или для ADMIN
        [HttpPut("{id:long}")]
        [Authorize(Roles = "ADMIN,CUSTOMER")]
        public async Task<ActionResult<EditorResponseTo>> UpdateEditor(long id, [FromBody] EditorRequestTo request)
        {
            // Проверка прав
            if (!IsAdmin() && GetCurrentUserId() != id)
                return Forbid();

            request.Id = id;
            var updated = await _editorService.UpdateEditor(request);
            return Ok(updated);
        }

        // DELETE – только ADMIN
        [HttpDelete("{id:long}")]
        [Authorize(Roles = "ADMIN")]
        public async Task<IActionResult> DeleteEditor(long id)
        {
            await _editorService.DeleteEditor(new EditorRequestTo { Id = id });
            return NoContent();
        }

        private long GetCurrentUserId()
        {
            var idClaim = User.FindFirst("id");
            return idClaim != null ? long.Parse(idClaim.Value) : 0;
        }

        private bool IsAdmin()
        {
            return User.IsInRole("ADMIN");
        }
    }
}