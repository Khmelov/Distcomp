using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;

namespace DC_REST.Services.Interfaces
{
    public interface INoteService
	{
		NoteResponseTo CreateNote(NoteRequestTo noteRequestDto);
		NoteResponseTo GetNoteById(int id);
		List<NoteResponseTo> GetAllNotes();
		NoteResponseTo UpdateNote(int id, NoteRequestTo noteRequestDto);
		bool DeleteNote(int id);
	}
}
