using AutoMapper;
using MediatR;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Articles.Commands;

public record UpdateArticleCommand(long Id, ArticleRequestTo Dto) : IRequest<ArticleResponseTo>;

public class UpdateArticleHandler : IRequestHandler<UpdateArticleCommand, ArticleResponseTo>
{
    private readonly IRepository<Article> _articleRepository;
    private readonly IRepository<Author> _authorRepository;
    private readonly IMapper _mapper;

    public UpdateArticleHandler(IRepository<Article> articleRepository, IRepository<Author> authorRepository, IMapper mapper)
    {
        _articleRepository = articleRepository;
        _authorRepository = authorRepository;
        _mapper = mapper;
    }

    public async Task<ArticleResponseTo> Handle(UpdateArticleCommand request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrEmpty(request.Dto.Title) || request.Dto.Title.Length < 2 || request.Dto.Title.Length > 64)
            throw new ValidationException("Title must be between 2 and 64 characters.");
        if (string.IsNullOrEmpty(request.Dto.Content) || request.Dto.Content.Length < 4 || request.Dto.Content.Length > 2048)
            throw new ValidationException("Content must be between 4 and 2048 characters.");

        var author = await _authorRepository.GetByIdAsync(request.Dto.AuthorId)
            ?? throw new NotFoundException("Author", request.Dto.AuthorId);

        var entity = _mapper.Map<Article>(request.Dto);
        entity.Id = request.Id;
        entity.Modified = DateTime.UtcNow;

        var updated = await _articleRepository.UpdateAsync(entity)
            ?? throw new NotFoundException("Article", request.Id);
        return _mapper.Map<ArticleResponseTo>(updated);
    }
}
