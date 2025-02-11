using Application.DTO.Response;
using MediatR;

namespace Application.Features.News.Queries;

public record ReadManyNewsQuery() : IRequest<IEnumerable<NewsResponseTo>>;