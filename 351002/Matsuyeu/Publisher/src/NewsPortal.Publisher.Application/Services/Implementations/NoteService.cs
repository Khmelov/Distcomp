using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Implementations
{
    public class NoteService : INoteService
    {
        private readonly IDiscussionApiClient _discussionApiClient;
        private static readonly Dictionary<long, NoteResponseTo> _noteCache = new();

        public NoteService(IDiscussionApiClient discussionApiClient)
        {
            _discussionApiClient = discussionApiClient;
        }

        public async Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync()
        {
            // Для простоты возвращаем из кеша
            return _noteCache.Values;
        }

        public async Task<NoteResponseTo> GetNoteByIdAsync(long id)
        {
            // Сначала проверяем кеш
            if (_noteCache.TryGetValue(id, out var cachedNote))
            {
                return cachedNote;
            }

            // Если нет в кеше, идем в Discussion API
            var note = await _discussionApiClient.GetNoteByIdAsync(id);
            if (note == null)
                throw new NotFoundException($"Note with ID {id} not found");

            // Сохраняем в кеш
            _noteCache[id] = note;

            return note;
        }

        public async Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest)
        {
            var createdNote = await _discussionApiClient.CreateNoteAsync(noteRequest);

            // Сохраняем в кеш
            _noteCache[createdNote.Id] = createdNote;

            return createdNote;
        }

        public async Task UpdateNoteAsync(NoteRequestTo noteRequest)
        {
            // Обновляем в Discussion API
            var updatedNote = await _discussionApiClient.UpdateNoteAsync(noteRequest);

            // КРИТИЧЕСКИ ВАЖНО: Обновляем кеш после успешного обновления!
            if (updatedNote != null)
            {
                _noteCache[updatedNote.Id] = updatedNote;
            }
            else
            {
                // Если UpdateNoteAsync не возвращает заметку, получаем её отдельно
                var note = await _discussionApiClient.GetNoteByIdAsync(noteRequest.Id);
                if (note != null)
                {
                    _noteCache[note.Id] = note;
                }
            }
        }

        public async Task DeleteNoteAsync(long id)
        {
            var deleted = await _discussionApiClient.DeleteNoteAsync(id);
            if (!deleted)
                throw new NotFoundException($"Note with ID {id} not found");

            // Удаляем из кеша
            _noteCache.Remove(id);
        }
    }
}