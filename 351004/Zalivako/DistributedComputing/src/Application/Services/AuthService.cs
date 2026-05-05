using Application.Interfaces;
using Application.Specifications;
using Core.Entities;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace Application.Services
{
    public class AuthService : IAuthService
    {
        private readonly IEditorRepository _editorRepository;
        private readonly IConfiguration _configuration;

        public AuthService(IEditorRepository editorRepository, IConfiguration configuration)
        {
            _editorRepository = editorRepository;
            _configuration = configuration;
        }

        public async Task<string?> AuthenticateAsync(string login, string password)
        {
            // Ищем редактора по логину
            var spec = new EditorByLoginSpecification(login);
            var editor = await _editorRepository.FindAsync(spec);
            if (editor == null)
                return null;

            // Проверяем пароль через BCrypt
            if (!BCrypt.Net.BCrypt.Verify(password, editor.Password))
                return null;

            // Генерируем JWT
            return GenerateJwtToken(editor);
        }

        private string GenerateJwtToken(Editor editor)
        {
            var jwtSettings = _configuration.GetSection("Jwt");
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtSettings["Key"]));
            var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, editor.Login),
                new Claim("role", editor.Role),
                new Claim("id", editor.Id.ToString())
            };

            var token = new JwtSecurityToken(
                issuer: jwtSettings["Issuer"],
                audience: jwtSettings["Audience"],
                claims: claims,
                expires: DateTime.UtcNow.AddMinutes(Convert.ToDouble(jwtSettings["ExpiryMinutes"] ?? "60")),
                signingCredentials: credentials
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}