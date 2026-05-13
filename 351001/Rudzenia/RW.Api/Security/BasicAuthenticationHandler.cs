using System.Net.Http.Headers;
using System.Security.Claims;
using System.Text;
using System.Text.Encodings.Web;
using Microsoft.AspNetCore.Authentication;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using RW.Domain.Entities;
using RW.Infrastructure.Data;

namespace RW.Api.Security;

public class BasicAuthenticationHandler : AuthenticationHandler<AuthenticationSchemeOptions>
{
    public const string SchemeName = "Basic";

    private readonly IServiceScopeFactory _scopeFactory;

    public BasicAuthenticationHandler(
        IOptionsMonitor<AuthenticationSchemeOptions> options,
        ILoggerFactory logger,
        UrlEncoder encoder,
        IServiceScopeFactory scopeFactory)
        : base(options, logger, encoder)
    {
        _scopeFactory = scopeFactory;
    }

    protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        if (!Request.Headers.ContainsKey("Authorization"))
            return AuthenticateResult.NoResult();

        try
        {
            var header = AuthenticationHeaderValue.Parse(Request.Headers["Authorization"].ToString());
            if (!"Basic".Equals(header.Scheme, StringComparison.OrdinalIgnoreCase))
                return AuthenticateResult.NoResult();

            var credentialBytes = Convert.FromBase64String(header.Parameter ?? string.Empty);
            var credentials = Encoding.UTF8.GetString(credentialBytes).Split(':', 2);
            if (credentials.Length != 2)
                return AuthenticateResult.Fail("Invalid Basic credentials format");

            var login = credentials[0];
            var password = credentials[1];

            using var scope = _scopeFactory.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            var author = await db.Authors.AsNoTracking()
                .FirstOrDefaultAsync(a => a.Login == login);

            if (author is null)
                return AuthenticateResult.Fail("Invalid login or password");

            bool ok;
            try
            {
                ok = BCrypt.Net.BCrypt.Verify(password, author.Password);
            }
            catch
            {
                ok = author.Password == password;
            }

            if (!ok)
                return AuthenticateResult.Fail("Invalid login or password");

            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, author.Id.ToString()),
                new Claim(ClaimTypes.Name, author.Login),
                new Claim(ClaimTypes.Role, author.Role.ToString()),
                new Claim("role", author.Role.ToString())
            };
            var identity = new ClaimsIdentity(claims, Scheme.Name);
            var principal = new ClaimsPrincipal(identity);
            var ticket = new AuthenticationTicket(principal, Scheme.Name);

            return AuthenticateResult.Success(ticket);
        }
        catch (Exception ex)
        {
            return AuthenticateResult.Fail($"Authentication failed: {ex.Message}");
        }
    }

    protected override Task HandleChallengeAsync(AuthenticationProperties properties)
    {
        Response.Headers["WWW-Authenticate"] = "Basic realm=\"RW.Api\", charset=\"UTF-8\"";
        Response.StatusCode = StatusCodes.Status401Unauthorized;
        return Task.CompletedTask;
    }
}
