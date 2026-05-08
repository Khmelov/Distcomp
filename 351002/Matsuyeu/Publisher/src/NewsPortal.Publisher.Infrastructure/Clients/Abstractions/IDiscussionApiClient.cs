using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions
{
    public interface IDiscussionApiClient
    {
        Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync();
        Task<NoteResponseTo?> GetNoteByIdAsync(long id);
        Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest);
        Task<NoteResponseTo> UpdateNoteAsync(NoteRequestTo noteRequest);
        Task<bool> DeleteNoteAsync(long id);
        Task<bool> DeleteNotesByNewsIdAsync(long newsId);
    }
}
