using AutoMapper;
using Publisher.Application.ViewModel;
using Shared.Messages;

namespace Publisher.Presentation.Profiles;

public class ReactionProfile : Profile
{
    public ReactionProfile()
    {
        CreateMap<ReactionResponseViewModel, ReactionResponseMsg>().ReverseMap();
    }
}
