using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions
{
    public interface INoteService
    {
        Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync();
        Task<NoteResponseTo> GetNoteByIdAsync(long id);
        Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest);
        Task UpdateNoteAsync(NoteRequestTo noteRequest);
        Task DeleteNoteAsync(long id);
    }
}
