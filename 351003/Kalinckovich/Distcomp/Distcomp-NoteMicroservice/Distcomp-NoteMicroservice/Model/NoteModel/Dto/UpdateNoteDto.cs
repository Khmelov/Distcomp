using System.ComponentModel.DataAnnotations;

namespace Distcomp_NoteMicroservice.Model.NoteModel.Dto;

public record UpdateNoteDto(
    [property: Required(ErrorMessage = "Country is required")]
    string Country,

    [property: Required(ErrorMessage = "TopicId is required")]
    long TopicId,

    [property: Required(ErrorMessage = "Id is required")]
    long Id,

    [property: Required(ErrorMessage = "Content is required")]
    [property: MinLength(2, ErrorMessage = "Content must be at least 2 characters")]
    [property: MaxLength(2048, ErrorMessage = "Content cannot exceed 2048 characters")]
    string Content
);