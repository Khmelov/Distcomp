using Application.DTO.Request;
using Application.DTO.Response;
using MediatR;

namespace Application.Features.News.Commands;

public record CreateNewsCommand(NewsRequestTo NewsRequestTo) : IRequest<NewsResponseTo>;