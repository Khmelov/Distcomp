using Distcomp_NoteMicroservice.Model.NoteModel;
using Distcomp_NoteMicroservice.Model.NoteModel.Dto;

namespace Distcomp_NoteMicroservice.Validation.NoteValidator;

public interface INoteValidationService
{
    
    ValidationResult ValidateCreateNote(CreateNoteDto dto);
    
    ValidationResult ValidateUpdateNote(UpdateNoteDto dto);
    
    ValidationResult ValidateNote(Note note);
    
    ValidationResult ValidateCountry(string country);
    
    ValidationResult ValidateContent(string content);
    
    ValidationResult ValidateTopicId(long topicId);
    
    ValidationResult ValidateNoteId(long id);
}