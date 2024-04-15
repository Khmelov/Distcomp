using Discussion.DTOs.Request;
using Discussion.DTOs.Response;

namespace Discussion.Services.Interfaces
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
