public interface INoteService
{
    Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo notelDto);
    Task<NoteResponseTo> GetNoteByIdAsync(int id);
    Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync();
    Task<NoteResponseTo> UpdateNoteAsync(Note noteDto);
    Task<bool> DeleteNoteAsync(int id);
}
public class NoteService : INoteService
{
    private readonly IGenericRepository<Note> _noteRepository;
    private readonly IGenericRepository<Story> _storyRepository;

    public NoteService(
        IGenericRepository<Note> noteRepository,
        IGenericRepository<Story> storyRepository,
        ILogger<NoteService> logger)
    {
        _noteRepository = noteRepository;
        _storyRepository = storyRepository;
    }

    public async Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteDto)
    {
        var story = await _storyRepository.GetByIdAsync(noteDto.StoryId);
        if (story == null)
        {
            throw new KeyNotFoundException($"Story with ID {noteDto.StoryId} does not exist");
        }

        var note = new Note
        {
            storyId = noteDto.StoryId,
            Content = noteDto.Content,
        };

        var createdNote = await _noteRepository.AddAsync(note);
        return MapToResponse(createdNote);
    }

    public async Task<bool> DeleteNoteAsync(int id)
    {
        var note = await _noteRepository.GetByIdAsync(id);
        if (note == null)
        {
            return false;
        }

        var deleted = await _noteRepository.DeleteAsync(id);

        return deleted;
    }

    public async Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync()
    {
        var notes = await _noteRepository.GetAllAsync();
        return notes.Select(MapToResponse);
    }

    public async Task<NoteResponseTo> GetNoteByIdAsync(int id)
    {
        var note = await _noteRepository.GetByIdAsync(id);
        if (note == null)
        {
            throw new KeyNotFoundException("Note not found.");
        }

        return MapToResponse(note);
    }

    public async Task<NoteResponseTo> UpdateNoteAsync(Note noteDto)
    {
        var note = await _noteRepository.GetByIdAsync(noteDto.id);
        if (note == null)
            throw new KeyNotFoundException("Note not found.");

        note.Content = noteDto.Content;
                
        var updatedNote = await _noteRepository.UpdateAsync(note);
        return MapToResponse(updatedNote);
    }

    private NoteResponseTo MapToResponse(Note note)
    {
        return new NoteResponseTo
        {
            Id = note.id,
            StoryId = note.storyId,
            Content = note.Content
        };
    }
}



