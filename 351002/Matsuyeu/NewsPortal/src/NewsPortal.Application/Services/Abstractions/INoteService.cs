using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Repositories.Abstractions;

namespace NewsPortal.Services.Abstractions
{
    public interface INoteService
    {
        Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync();
        Task<NoteResponseTo?> GetNoteByIdAsync(long id);
        Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest);
        Task<bool> UpdateNoteAsync(NoteRequestTo noteRequest);
        Task<bool> DeleteNoteAsync(long id);
        Task<PagedResult<NoteResponseTo>> GetPagedNotesAsync(QueryParameters parameters);
        Task<IEnumerable<NoteResponseTo>> GetNotesByNewsIdAsync(long newsId);
    }
}
