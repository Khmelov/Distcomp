using AutoMapper;
using MediatR;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Articles.Commands;

public record CreateArticleCommand(ArticleRequestTo Dto) : IRequest<ArticleResponseTo>;

public class CreateArticleHandler : IRequestHandler<CreateArticleCommand, ArticleResponseTo>
{
    private readonly IRepository<Article> _articleRepository;
    private readonly IRepository<Author> _authorRepository;
    private readonly IRepository<Tag> _tagRepository;
    private readonly IMapper _mapper;

    public CreateArticleHandler(IRepository<Article> articleRepository, IRepository<Author> authorRepository, IRepository<Tag> tagRepository, IMapper mapper)
    {
        _articleRepository = articleRepository;
        _authorRepository = authorRepository;
        _tagRepository = tagRepository;
        _mapper = mapper;
    }

    public async Task<ArticleResponseTo> Handle(CreateArticleCommand request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrEmpty(request.Dto.Title) || request.Dto.Title.Length < 2 || request.Dto.Title.Length > 64)
            throw new Exceptions.ValidationException("Title must be between 2 and 64 characters.");
        if (string.IsNullOrEmpty(request.Dto.Content) || request.Dto.Content.Length < 4 || request.Dto.Content.Length > 2048)
            throw new Exceptions.ValidationException("Content must be between 4 and 2048 characters.");

        var author = await _authorRepository.GetByIdAsync(request.Dto.AuthorId)
            ?? throw new NotFoundException("Author", request.Dto.AuthorId);

        var entity = _mapper.Map<Article>(request.Dto);
        entity.Created = DateTime.UtcNow;
        entity.Modified = DateTime.UtcNow;

        foreach (var tagName in request.Dto.Tags)
        {
            entity.Tags.Add(new Tag { Name = tagName });
        }

        var created = await _articleRepository.CreateAsync(entity);
        return _mapper.Map<ArticleResponseTo>(created);
    }
}
