using Discussion.src.NewsPortal.Discussion.Application.Dtos.RequestTo;
using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Discussion.src.NewsPortal.Discussion.Application.Services.Abstractions;
using Discussion.src.NewsPortal.Discussion.Domain.Entities;
using Discussion.src.NewsPortal.Discussion.Domain.Exceptions;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Clients.Abstractions;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Repositories.Abstractions;

namespace Discussion.src.NewsPortal.Discussion.Application.Services.Implementations
{
    public class NoteService : INoteService
    {
        private readonly INoteRepository _noteRepository;
        private readonly IPublisherApiClient _publisherApiClient;

        public NoteService(INoteRepository noteRepository, IPublisherApiClient publisherApiClient)
        {
            _noteRepository = noteRepository;
            _publisherApiClient = publisherApiClient;
        }

        public async Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync()
        {
            var notes = await _noteRepository.GetAllAsync();
            var responseList = new List<NoteResponseTo>();

            foreach (var note in notes)
            {
                responseList.Add(await BuildResponseAsync(note));
            }

            return responseList;
        }

        public async Task<NoteResponseTo?> GetNoteByIdAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var note = await _noteRepository.GetByIdAsync(id);
            if (note == null)
                throw new NotFoundException($"Note with ID {id} not found");

            return await BuildResponseAsync(note);
        }

        public async Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest)
        {
           await ValidateNewsExistsAsync(noteRequest.NewsId);

            var note = new Note
            {
                NewsId = noteRequest.NewsId,
                Content = noteRequest.Content.Trim()
            };

            var createdNote = await _noteRepository.AddAsync(note);
            return await BuildResponseAsync(createdNote);
        }

        public async Task<bool> UpdateNoteAsync(NoteRequestTo noteRequest)
        {
            if (noteRequest.Id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var existingNote = await _noteRepository.GetByIdAsync(noteRequest.Id);
            if (existingNote == null)
                throw new NotFoundException($"Note with ID {noteRequest.Id} not found");

            // Проверяем существование News, если ID изменился
            if (existingNote.NewsId != noteRequest.NewsId)
            {
                await ValidateNewsExistsAsync(noteRequest.NewsId);
            }

            // Обновляем поля
            existingNote.NewsId = noteRequest.NewsId;
            existingNote.Content = noteRequest.Content.Trim();

            await _noteRepository.UpdateAsync(existingNote);
            return true;
        }

        public async Task<bool> DeleteNoteAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var existingNote = await _noteRepository.GetByIdAsync(id);
            if (existingNote == null)
                throw new NotFoundException($"Note with ID {id} not found");

            await _noteRepository.DeleteAsync(id);
            return true;
        }

        #region Private Methods

        private async Task ValidateNewsExistsAsync(long newsId)
        {
            var newsExists = await _publisherApiClient.NewsExistsAsync(newsId);
            if (!newsExists)
                throw new NotFoundException($"News with ID {newsId} does not exist");
        }

        private async Task<NoteResponseTo> BuildResponseAsync(Note note)
        {

            return new NoteResponseTo
            {
                Id = note.Id,
                NewsId = note.NewsId,
                Content = note.Content
            };
        }

        #endregion
    }
}