using Application.DTO.Request;
using Application.DTO.Response;
using MediatR;

namespace Application.Features.News.Commands;

public record UpdateNewsCommand(long Id, NewsRequestTo NewsRequestTo) : IRequest<NewsResponseTo>;