using AutoMapper;
using Discussion.DTOs.Request;
using Discussion.DTOs.Response;
using Discussion.Entities;

namespace Discussion.Services.Mappers
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
