using Discussion.src.NewsPortal.Discussion.Application.Dtos.RequestTo;
using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;

namespace Discussion.src.NewsPortal.Discussion.Application.Services.Abstractions
{
    public interface INoteService
    {
        Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync();
        Task<NoteResponseTo?> GetNoteByIdAsync(long id);
        Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest);
        Task<bool> UpdateNoteAsync(NoteRequestTo noteRequest);
        Task<bool> DeleteNoteAsync(long id);
    }
}
