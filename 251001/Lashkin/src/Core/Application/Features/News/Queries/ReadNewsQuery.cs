using Application.DTO.Response;
using MediatR;

namespace Application.Features.News.Queries;

public record ReadNewsQuery(long Id) : IRequest<NewsResponseTo>;