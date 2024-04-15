using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;

namespace DC_REST.Services.Mappers
{
	public class NoteMapper: Profile
	{
		public NoteMapper() 
		{
			CreateMap<Note, NoteResponseTo>();
			CreateMap<NoteRequestTo, Note>();
		}
	}
}
