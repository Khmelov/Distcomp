using System.Text.RegularExpressions;
using Distcomp_NoteMicroservice.Model.NoteModel;
using Distcomp_NoteMicroservice.Model.NoteModel.Dto;

namespace Distcomp_NoteMicroservice.Validation.NoteValidator;

public class NoteValidationService : INoteValidationService
{
    private const int MinContentLength = 2;
    private const int MaxContentLength = 2048;
    private const int MinCountryLength = 2;
    private const int MaxCountryLength = 3;  
    
    private static readonly Regex CountryCodePattern = new(@"^[A-Z]{2,3}$", RegexOptions.Compiled);

    public ValidationResult ValidateCreateNote(CreateNoteDto dto)
    {
        List<ValidationError> errors = new List<ValidationError>();

        
        ValidationResult countryResult = ValidateCountry(dto.Country);
        if (!countryResult.IsValid)
        {
            errors.AddRange(countryResult.Errors);
        }


        ValidationResult topicResult = ValidateTopicId(dto.TopicId);
        if (!topicResult.IsValid)
        {
            errors.AddRange(topicResult.Errors);
        }


        ValidationResult contentResult = ValidateContent(dto.Content);
        if (!contentResult.IsValid)
        {
            errors.AddRange(contentResult.Errors);
        }

        return errors.Any() 
            ? ValidationResult.Failure(errors.ToArray()) 
            : ValidationResult.Success;
    }

    public ValidationResult ValidateUpdateNote(UpdateNoteDto dto)
    {
        var errors = new List<ValidationError>();

        
        var countryResult = ValidateCountry(dto.Country);
        if (!countryResult.IsValid)
            errors.AddRange(countryResult.Errors);

        
        var topicResult = ValidateTopicId(dto.TopicId);
        if (!topicResult.IsValid)
            errors.AddRange(topicResult.Errors);

        
        var idResult = ValidateNoteId(dto.Id);
        if (!idResult.IsValid)
            errors.AddRange(idResult.Errors);

        
        var contentResult = ValidateContent(dto.Content);
        if (!contentResult.IsValid)
            errors.AddRange(contentResult.Errors);

        return errors.Any() 
            ? ValidationResult.Failure(errors.ToArray()) 
            : ValidationResult.Success;
    }

    public ValidationResult ValidateNote(Note note)
    {
        var errors = new List<ValidationError>();

        
        var countryResult = ValidateCountry(note.Country);
        if (!countryResult.IsValid)
            errors.AddRange(countryResult.Errors);

        
        var topicResult = ValidateTopicId(note.TopicId);
        if (!topicResult.IsValid)
            errors.AddRange(topicResult.Errors);

        
        var idResult = ValidateNoteId(note.Id);
        if (!idResult.IsValid)
            errors.AddRange(idResult.Errors);

        
        var contentResult = ValidateContent(note.Content);
        if (!contentResult.IsValid)
            errors.AddRange(contentResult.Errors);

        
        if (note.CreatedAt == default)
        {
            errors.Add(new ValidationError(
                nameof(note.CreatedAt),
                "CreatedAt must be set",
                "NOTE_CREATED_AT_REQUIRED"));
        }

        return errors.Any() 
            ? ValidationResult.Failure(errors.ToArray()) 
            : ValidationResult.Success;
    }

    public ValidationResult ValidateCountry(string country)
    {
        var errors = new List<ValidationError>();

        if (string.IsNullOrWhiteSpace(country))
        {
            errors.Add(new ValidationError(
                "country",
                "Country is required",
                "NOTE_COUNTRY_REQUIRED"));
        }
        else if (country.Length < MinCountryLength || country.Length > MaxCountryLength)
        {
            errors.Add(new ValidationError(
                "country",
                $"Country must be {MinCountryLength}-{MaxCountryLength} characters (ISO 3166-1)",
                "NOTE_COUNTRY_INVALID_LENGTH"));
        }
        else if (!CountryCodePattern.IsMatch(country.ToUpperInvariant()))
        {
            errors.Add(new ValidationError(
                "country",
                "Country must be valid ISO 3166-1 alpha-2 or alpha-3 code (e.g., US, USA, DE)",
                "NOTE_COUNTRY_INVALID_FORMAT"));
        }

        return errors.Any() 
            ? ValidationResult.Failure(errors.ToArray()) 
            : ValidationResult.Success;
    }

    public ValidationResult ValidateContent(string content)
    {
        var errors = new List<ValidationError>();

        if (string.IsNullOrWhiteSpace(content))
        {
            errors.Add(new ValidationError(
                "content",
                "Content is required",
                "NOTE_CONTENT_REQUIRED"));
        }
        else if (content.Length < MinContentLength)
        {
            errors.Add(new ValidationError(
                "content",
                $"Content must be at least {MinContentLength} characters",
                "NOTE_CONTENT_TOO_SHORT"));
        }
        else if (content.Length > MaxContentLength)
        {
            errors.Add(new ValidationError(
                "content",
                $"Content cannot exceed {MaxContentLength} characters",
                "NOTE_CONTENT_TOO_LONG"));
        }

        return errors.Any() 
            ? ValidationResult.Failure(errors.ToArray()) 
            : ValidationResult.Success;
    }

    public ValidationResult ValidateTopicId(long topicId)
    {
        if (topicId <= 0)
        {
            return ValidationResult.Failure(
                "topicId",
                "TopicId must be a positive number",
                "NOTE_TOPIC_ID_INVALID");
        }

        return ValidationResult.Success;
    }

    public ValidationResult ValidateNoteId(long id)
    {
        if (id <= 0)
        {
            return ValidationResult.Failure(
                "id",
                "Id must be a positive number",
                "NOTE_ID_INVALID");
        }

        return ValidationResult.Success;
    }
}