using Microsoft.AspNetCore.Identity.Data;
using Microsoft.AspNetCore.Mvc;
using Publisher.Dto;
using Publisher.Service;

namespace Publisher.Controller {
    [ApiController]
    [Route("api/v2.0")]
    public class AuthController : ControllerBase {
        private readonly EditorService _editorService;
        private readonly TokenService _tokenService;

        public AuthController(EditorService editorService, TokenService tokenService) {
            _editorService = editorService;
            _tokenService = tokenService;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequestTo login) {
            var editor = await _editorService.GetByLoginAsync(login.Login);
            if (editor == null || !BCrypt.Net.BCrypt.Verify(login.Password, editor.Password)) {
                return Unauthorized(new { errorMessage = "Invalid login or password", errorCode = 40101 });
            }

            var token = _tokenService.GenerateToken(editor);
            return Ok(new { access_token = token, type_token = "Bearer" });
        }

        [HttpPost("editors")]
        public async Task<IActionResult> Register([FromBody] EditorRequestTo request) {
            request.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);
            var newEditor = await _editorService.AddAsync(request);
            return CreatedAtAction("GetEditorV2", "Editor", new { id = newEditor.Id }, newEditor);
        }
    }
}
