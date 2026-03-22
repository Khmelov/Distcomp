using Distcomp_NoteMicroservice.Model.NoteModel.Dto;
using Distcomp_NoteMicroservice.Validation;

namespace Distcomp_NoteMicroservice.Service;

public interface INoteService
{
    Task<Result<NoteResponseDto>> GetNoteByIdAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default);

    Task<Result<IEnumerable<NoteResponseDto>>> GetNotesByCountryAndTopicAsync(
        string country, 
        long topicId,
        long? minId = null,
        long? maxId = null,
        int limit = 100,
        CancellationToken cancellationToken = default);

    Task<Result<NoteResponseDto>> CreateNoteAsync(
        CreateNoteDto dto, 
        CancellationToken cancellationToken = default);

    Task<Result<NoteResponseDto>> UpdateNoteAsync(
        UpdateNoteDto dto, 
        CancellationToken cancellationToken = default);

    Task<Result<bool>> DeleteNoteAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default);
}