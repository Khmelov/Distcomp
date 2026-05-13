using AutoMapper;
using Discussion.Application.ViewModels;
using Discussion.Presentation.Contracts;
using Shared.Messages;

namespace Discussion.Presentation.Profiles;

public class ReactionProfile : Profile
{
    public ReactionProfile()
    {
        CreateMap<ReactionResponseViewModel, ReactionResponse>().ReverseMap();
        CreateMap<ReactionResponseViewModel, ReactionResponseMsg>().ReverseMap();
    }
}
