using AutoMapper;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Models.Entities;

namespace lab1.Mapping
{
    public class LabelProfile : Profile
    {
        public LabelProfile()
        {
            CreateMap<LabelRequestTo, Label>();
            CreateMap<Label, LabelResponseTo>();
        }
    }
}
