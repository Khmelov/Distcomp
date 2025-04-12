using MediatR;

namespace Application.Features.News.Commands;

public record DeleteNewsCommand(long Id) : IRequest;