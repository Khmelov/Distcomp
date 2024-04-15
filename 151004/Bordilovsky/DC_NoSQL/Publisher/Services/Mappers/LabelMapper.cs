using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;

namespace DC_REST.Services.Mappers
{
	public class LabelMapper : Profile
	{
		public LabelMapper() 
		{
			CreateMap<Label, LabelResponseTo>();
			CreateMap<LabelRequestTo, Label>();
		}
	}
}
