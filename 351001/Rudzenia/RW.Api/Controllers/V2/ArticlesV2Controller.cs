using MediatR;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using RW.Application.DTOs.Request;
using RW.Application.Exceptions;
using RW.Application.Features.Articles.Commands;
using RW.Application.Features.Articles.Queries;
using RW.Infrastructure.Data;

namespace RW.Api.Controllers.V2;

[ApiController]
[Route("api/v2.0/articles")]
[Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
public class ArticlesV2Controller : ControllerBase
{
    private readonly ISender _sender;
    private readonly ApplicationDbContext _db;

    public ArticlesV2Controller(ISender sender, ApplicationDbContext db)
    {
        _sender = sender;
        _db = db;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var result = await _sender.Send(new GetArticlesQuery());
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        var result = await _sender.Send(new GetArticleByIdQuery(id));
        return Ok(result);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] ArticleRequestTo dto)
    {
        EnsureCanWriteArticle(dto.AuthorId);
        var result = await _sender.Send(new CreateArticleCommand(dto));
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] ArticleRequestTo dto)
    {
        await EnsureCanModifyExistingArticleAsync(dto.Id);
        var result = await _sender.Send(new UpdateArticleCommand(dto.Id, dto));
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await EnsureCanModifyExistingArticleAsync(id);
        await _sender.Send(new DeleteArticleCommand(id));
        return NoContent();
    }

    private void EnsureCanWriteArticle(long authorId)
    {
        if (User.IsInRole("ADMIN")) return;
        var idClaim = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
        if (long.TryParse(idClaim, out var currentId) && currentId == authorId) return;
        throw new ForbiddenException("Customers can publish only their own articles.");
    }

    private async Task EnsureCanModifyExistingArticleAsync(long articleId)
    {
        if (User.IsInRole("ADMIN")) return;

        var article = await _db.Articles.AsNoTracking().FirstOrDefaultAsync(a => a.Id == articleId)
            ?? throw new NotFoundException("Article", articleId);

        var idClaim = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
        if (long.TryParse(idClaim, out var currentId) && currentId == article.AuthorId) return;

        throw new ForbiddenException("Customers can modify only their own articles.");
    }
}
