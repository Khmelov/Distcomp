using Distcomp_NoteMicroservice.Model.NoteModel;
using Distcomp_NoteMicroservice.Model.NoteModel.Dto;
using Distcomp_NoteMicroservice.Repository;
using Distcomp_NoteMicroservice.Validation;
using Distcomp_NoteMicroservice.Validation.NoteValidator;

namespace Distcomp_NoteMicroservice.Service;

public class NoteService : INoteService
{
    private readonly INoteRepository _noteRepository;
    private readonly INoteValidationService _validationService;
    private readonly ILogger<NoteService> _logger;

    public NoteService(
        INoteRepository noteRepository,
        INoteValidationService validationService,
        ILogger<NoteService> logger)
    {
        _noteRepository = noteRepository;
        _validationService = validationService;
        _logger = logger;
    }

    public async Task<Result<NoteResponseDto>> GetNoteByIdAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogDebug(
            "Getting note by ID: country={Country}, topicId={TopicId}, id={Id}",
            country, topicId, id);

        
        var validationErrors = new List<ValidationError>();

        var countryResult = _validationService.ValidateCountry(country);
        if (!countryResult.IsValid)
            validationErrors.AddRange(countryResult.Errors);

        var topicResult = _validationService.ValidateTopicId(topicId);
        if (!topicResult.IsValid)
            validationErrors.AddRange(topicResult.Errors);

        var idResult = _validationService.ValidateNoteId(id);
        if (!idResult.IsValid)
            validationErrors.AddRange(idResult.Errors);

        
        if (validationErrors.Any())
        {
            _logger.LogWarning(
                "Validation failed for GetNoteById: {Errors}",
                string.Join("; ", validationErrors.Select(e => e.ToString())));

            return Result<NoteResponseDto>.Failure(validationErrors.ToArray());
        }

        
        try
        {
            var note = await _noteRepository.GetNoteByIdAsync(country, topicId, id, cancellationToken);

            if (note == null)
            {
                _logger.LogDebug(
                    "Note not found: country={Country}, topicId={TopicId}, id={Id}",
                    country, topicId, id);

                return Result<NoteResponseDto>.Failure(
                    "id",
                    $"Note not found: country={country}, topicId={topicId}, id={id}",
                    "NOTE_NOT_FOUND");
            }

            return Result<NoteResponseDto>.Success(MapToResponseDto(note));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error getting note: country={Country}, topicId={TopicId}, id={Id}",
                country, topicId, id);

            return Result<NoteResponseDto>.Failure(
                "system",
                "An error occurred while fetching the note",
                "NOTE_FETCH_ERROR");
        }
    }

    public async Task<Result<IEnumerable<NoteResponseDto>>> GetNotesByCountryAndTopicAsync(
        string country, 
        long topicId,
        long? minId = null,
        long? maxId = null,
        int limit = 100,
        CancellationToken cancellationToken = default)
    {
        _logger.LogDebug(
            "Getting notes: country={Country}, topicId={TopicId}, minId={MinId}, maxId={MaxId}, limit={Limit}",
            country, topicId, minId, maxId, limit);

        
        var validationErrors = new List<ValidationError>();

        var countryResult = _validationService.ValidateCountry(country);
        if (!countryResult.IsValid)
            validationErrors.AddRange(countryResult.Errors);

        var topicResult = _validationService.ValidateTopicId(topicId);
        if (!topicResult.IsValid)
            validationErrors.AddRange(topicResult.Errors);

        if (minId.HasValue)
        {
            var minIdResult = _validationService.ValidateNoteId(minId.Value);
            if (!minIdResult.IsValid)
                validationErrors.AddRange(minIdResult.Errors);
        }

        if (maxId.HasValue)
        {
            var maxIdResult = _validationService.ValidateNoteId(maxId.Value);
            if (!maxIdResult.IsValid)
                validationErrors.AddRange(maxIdResult.Errors);
        }

        if (limit <= 0 || limit > 1000)
        {
            validationErrors.Add(new ValidationError(
                "limit",
                "Limit must be between 1 and 1000",
                "NOTE_LIMIT_INVALID"));
        }

        
        if (validationErrors.Any())
        {
            _logger.LogWarning(
                "Validation failed for GetNotesByCountryAndTopic: {Errors}",
                string.Join("; ", validationErrors.Select(e => e.ToString())));

            return Result<IEnumerable<NoteResponseDto>>.Failure(validationErrors.ToArray());
        }

        
        try
        {
            var notes = await _noteRepository.GetNotesByCountryAndTopicAsync(
                country, topicId, minId, maxId, limit, cancellationToken);

            return Result<IEnumerable<NoteResponseDto>>.Success(
                notes.Select(MapToResponseDto));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error getting notes: country={Country}, topicId={TopicId}",
                country, topicId);

            return Result<IEnumerable<NoteResponseDto>>.Failure(
                "system",
                "An error occurred while fetching notes",
                "NOTE_FETCH_ERROR");
        }
    }

    public async Task<Result<NoteResponseDto>> CreateNoteAsync(
        CreateNoteDto dto, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation("Creating note: country={Country}, topicId={TopicId}", dto.Country, dto.TopicId);

        
        var validationResult = _validationService.ValidateCreateNote(dto);
        if (!validationResult.IsValid)
        {
            _logger.LogWarning(
                "Validation failed for CreateNote: {Errors}",
                string.Join("; ", validationResult.Errors.Select(e => e.ToString())));

            return Result<NoteResponseDto>.Failure(validationResult.Errors.ToArray());
        }

        
        var note = new Note
        {
            Country = dto.Country.ToUpperInvariant(),  
            TopicId = dto.TopicId,
            Id = GenerateNoteId(),  
            Content = dto.Content
        };

        
        var entityValidationResult = _validationService.ValidateNote(note);
        if (!entityValidationResult.IsValid)
        {
            return Result<NoteResponseDto>.Failure(entityValidationResult.Errors.ToArray());
        }

        
        try
        {
            var createdNote = await _noteRepository.CreateAsync(note, cancellationToken);

            _logger.LogInformation(
                "Note created successfully: country={Country}, topicId={TopicId}, id={Id}",
                createdNote.Country, createdNote.TopicId, createdNote.Id);

            return Result<NoteResponseDto>.Success(MapToResponseDto(createdNote));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error creating note: country={Country}, topicId={TopicId}",
                dto.Country, dto.TopicId);

            return Result<NoteResponseDto>.Failure(
                "system",
                "An error occurred while creating the note",
                "NOTE_CREATE_ERROR");
        }
    }

    public async Task<Result<NoteResponseDto>> UpdateNoteAsync(
        UpdateNoteDto dto, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation(
            "Updating note: country={Country}, topicId={TopicId}, id={Id}",
            dto.Country, dto.TopicId, dto.Id);

        
        var validationResult = _validationService.ValidateUpdateNote(dto);
        if (!validationResult.IsValid)
        {
            _logger.LogWarning(
                "Validation failed for UpdateNote: {Errors}",
                string.Join("; ", validationResult.Errors.Select(e => e.ToString())));

            return Result<NoteResponseDto>.Failure(validationResult.Errors.ToArray());
        }

        
        var existingNote = await _noteRepository.GetNoteByIdAsync(
            dto.Country, dto.TopicId, dto.Id, cancellationToken);

        if (existingNote == null)
        {
            return Result<NoteResponseDto>.Failure(
                "id",
                $"Note not found: country={dto.Country}, topicId={dto.TopicId}, id={dto.Id}",
                "NOTE_NOT_FOUND");
        }

        
        existingNote.Content = dto.Content;

        
        var entityValidationResult = _validationService.ValidateNote(existingNote);
        if (!entityValidationResult.IsValid)
        {
            return Result<NoteResponseDto>.Failure(entityValidationResult.Errors.ToArray());
        }

        
        try
        {
            var updatedNote = await _noteRepository.UpdateAsync(existingNote, cancellationToken);

            _logger.LogInformation(
                "Note updated successfully: country={Country}, topicId={TopicId}, id={Id}",
                updatedNote.Country, updatedNote.TopicId, updatedNote.Id);

            return Result<NoteResponseDto>.Success(MapToResponseDto(updatedNote));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error updating note: country={Country}, topicId={TopicId}, id={Id}",
                dto.Country, dto.TopicId, dto.Id);

            return Result<NoteResponseDto>.Failure(
                "system",
                "An error occurred while updating the note",
                "NOTE_UPDATE_ERROR");
        }
    }

    public async Task<Result<bool>> DeleteNoteAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation(
            "Deleting note: country={Country}, topicId={TopicId}, id={Id}",
            country, topicId, id);

        
        var validationErrors = new List<ValidationError>();

        var countryResult = _validationService.ValidateCountry(country);
        if (!countryResult.IsValid)
            validationErrors.AddRange(countryResult.Errors);

        var topicResult = _validationService.ValidateTopicId(topicId);
        if (!topicResult.IsValid)
            validationErrors.AddRange(topicResult.Errors);

        var idResult = _validationService.ValidateNoteId(id);
        if (!idResult.IsValid)
            validationErrors.AddRange(idResult.Errors);

        
        if (validationErrors.Any())
        {
            _logger.LogWarning(
                "Validation failed for DeleteNote: {Errors}",
                string.Join("; ", validationErrors.Select(e => e.ToString())));

            return Result<bool>.Failure(validationErrors.ToArray());
        }

        
        try
        {
            var deleted = await _noteRepository.DeleteAsync(country, topicId, id, cancellationToken);

            if (!deleted)
            {
                return Result<bool>.Failure(
                    "id",
                    $"Note not found: country={country}, topicId={topicId}, id={id}",
                    "NOTE_NOT_FOUND");
            }

            _logger.LogInformation(
                "Note deleted successfully: country={Country}, topicId={TopicId}, id={Id}",
                country, topicId, id);

            return Result<bool>.Success(true);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error deleting note: country={Country}, topicId={TopicId}, id={Id}",
                country, topicId, id);

            return Result<bool>.Failure(
                "system",
                "An error occurred while deleting the note",
                "NOTE_DELETE_ERROR");
        }
    }

    
    private static NoteResponseDto MapToResponseDto(Note note) => new(
        note.Country,
        note.TopicId,
        note.Id,
        note.Content,
        note.CreatedAt,
        note.UpdatedAt
    );
    
    private static long GenerateNoteId() => DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
}