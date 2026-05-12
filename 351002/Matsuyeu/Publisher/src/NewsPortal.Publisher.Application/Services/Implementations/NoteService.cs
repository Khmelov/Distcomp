using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Implementations
{
    // Publisher.Application/Services/Implementations/NoteService.cs
    public class NoteService : INoteService
    {
        private readonly IDiscussionApiClient _discussionApiClient;
        //private readonly IMapper _mapper;

        public NoteService(IDiscussionApiClient discussionApiClient)//, IMapper mapper)
        {
            _discussionApiClient = discussionApiClient;
            //_mapper = mapper;
        }

        public async Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync()
        {
            var notes = await _discussionApiClient.GetAllNotesAsync();
            return notes;
            //return _mapper.Map<IEnumerable<NoteResponseTo>>(notes);
        }

        public async Task<NoteResponseTo> GetNoteByIdAsync(long id)
        {
            var note = await _discussionApiClient.GetNoteByIdAsync(id);
            if (note == null)
                throw new NotFoundException($"Note with ID {id} not found");

            return note;
            //return _mapper.Map<NoteResponseTo>(note);
        }

        public async Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest)
        {
            //var requestDto = _mapper.Map<NoteRequestTo>(noteRequest);
            var createdNote = await _discussionApiClient.CreateNoteAsync(noteRequest);
            //return _mapper.Map<NoteResponseTo>(createdNote);
            return createdNote;
        }

        public async Task UpdateNoteAsync(NoteRequestTo noteRequest)
        {
            //var requestDto = _mapper.Map<NoteRequestTo>(noteRequest);
            await _discussionApiClient.UpdateNoteAsync(noteRequest);
        }

        public async Task DeleteNoteAsync(long id)
        {
            var deleted = await _discussionApiClient.DeleteNoteAsync(id);
            if (!deleted)
                throw new NotFoundException($"Note with ID {id} not found");
        }
    }
}
