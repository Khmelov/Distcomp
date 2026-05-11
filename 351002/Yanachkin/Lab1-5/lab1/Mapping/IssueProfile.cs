using AutoMapper;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Models.Entities;

namespace lab1.Mapping;

public class IssueProfile : Profile
{
    public IssueProfile()
    {
        CreateMap<IssueRequestTo, Issue>()
            .ForMember(d => d.Labels, o => o.Ignore())
            .ForMember(d => d.Editor, o => o.Ignore());

        CreateMap<Issue, IssueResponseTo>()
            .ForMember(
                d => d.LabelIds,
                o => o.MapFrom(s => s.Labels.OrderBy(l => l.Id).Select(l => l.Id).ToList()));
    }
}
