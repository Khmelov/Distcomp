using DiscussionModule.DTOs.requests;
using DiscussionModule.DTOs.responses;

namespace DiscussionModule.interfaces;

public interface INoteService
{
    Task<NoteResponseTo> CreateNote(NoteRequestTo note);
    Task<IEnumerable<NoteResponseTo>> GetAllNotes();
    Task<NoteResponseTo?> GetNote(NoteRequestTo note);
    Task<NoteResponseTo?> UpdateNote(NoteRequestTo note);
    Task DeleteNote(NoteRequestTo note);
}