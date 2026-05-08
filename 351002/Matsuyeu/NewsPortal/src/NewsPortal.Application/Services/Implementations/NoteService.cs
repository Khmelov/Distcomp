using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Entities;
using NewsPortal.Models.Exceptions;
using NewsPortal.Models.Repositories.Abstractions;
using NewsPortal.Services.Abstractions;

namespace NewsPortal.Services.Implementations
{
    public class NoteService : INoteService
    {
        private readonly IRepository<Note> _noteRepository;
        private readonly IRepository<News> _newsRepository;

        public NoteService(IRepository<Note> noteRepository, IRepository<News> newsRepository)
        {
            _noteRepository = noteRepository;
            _newsRepository = newsRepository;
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
            // Проверка существования News
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

        public async Task<PagedResult<NoteResponseTo>> GetPagedNotesAsync(QueryParameters parameters)
        {
            var pagedResult = await _noteRepository.GetPagedAsync(parameters);

            var items = new List<NoteResponseTo>();
            foreach (var note in pagedResult.Items)
            {
                items.Add(await BuildResponseAsync(note));
            }

            return new PagedResult<NoteResponseTo>
            {
                Items = items,
                TotalCount = pagedResult.TotalCount,
                PageNumber = pagedResult.PageNumber,
                PageSize = pagedResult.PageSize
            };
        }

        public async Task<IEnumerable<NoteResponseTo>> GetNotesByNewsIdAsync(long newsId)
        {
            if (newsId <= 0)
                throw new BadRequestException("NewsId must be greater than 0");

            await ValidateNewsExistsAsync(newsId);

            var notes = await _noteRepository.FindAsync(n => n.NewsId == newsId);
            var responseList = new List<NoteResponseTo>();

            foreach (var note in notes)
            {
                responseList.Add(await BuildResponseAsync(note));
            }

            return responseList;
        }

        #region Private Methods

        private async Task ValidateNewsExistsAsync(long newsId)
        {
            var news = await _newsRepository.GetByIdAsync(newsId);
            if (news == null)
                throw new NotFoundException($"News with ID {newsId} does not exist");
        }

        private async Task<NoteResponseTo> BuildResponseAsync(Note note)
        {
            var news = await _newsRepository.GetByIdAsync(note.NewsId);

            return new NoteResponseTo
            {
                Id = note.Id,
                NewsId = note.NewsId,
                Content = note.Content,
                NewsTitle = news?.Title ?? string.Empty
            };
        }

        #endregion
    }
}