using AutoMapper;
using Discussion.Application.Features.Commands;
using Discussion.Application.ViewModels;
using Discussion.Domain.Models;

namespace Discussion.Application.Profiles;

public class ReactionApplicationProfile : Profile
{
    public ReactionApplicationProfile()
    {
        CreateMap<Reaction, ReactionResponseViewModel>().ReverseMap();
        CreateMap<Reaction, AddReactionCommand>().ReverseMap();
    }
}
