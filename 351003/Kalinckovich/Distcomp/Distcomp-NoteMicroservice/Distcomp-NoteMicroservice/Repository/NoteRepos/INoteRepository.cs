using Distcomp_NoteMicroservice.Model.NoteModel;

namespace Distcomp_NoteMicroservice.Repository;

public interface INoteRepository
{
    Task<Note?> GetNoteByIdAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default);

    Task<IEnumerable<Note>> GetNotesByCountryAndTopicAsync(
        string country, 
        long topicId,
        long? minId = null,
        long? maxId = null,
        int limit = 100,
        CancellationToken cancellationToken = default);

    Task<IEnumerable<Note>> GetNotesByCountryAndTopicRangeAsync(
        string country, 
        long minTopicId, 
        long maxTopicId,
        int limit = 100,
        CancellationToken cancellationToken = default);

    Task<Note> CreateAsync(
        Note note, 
        CancellationToken cancellationToken = default);

    Task<Note> UpdateAsync(
        Note note, 
        CancellationToken cancellationToken = default);

    Task<bool> DeleteAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default);
}