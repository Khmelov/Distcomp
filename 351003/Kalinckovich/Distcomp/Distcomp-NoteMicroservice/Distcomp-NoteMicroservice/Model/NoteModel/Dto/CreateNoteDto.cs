using System.ComponentModel.DataAnnotations;


public record CreateNoteDto(
    [property: Required(ErrorMessage = "Country is required")]
    [property: MinLength(2, ErrorMessage = "Country must be at least 2 characters")]
    [property: MaxLength(3, ErrorMessage = "Country cannot exceed 3 characters")]
    string Country,

    [property: Range(1, long.MaxValue, ErrorMessage = "TopicId must be positive")]
    long TopicId,

    [property: Required(ErrorMessage = "Content is required")]
    [property: MinLength(2, ErrorMessage = "Content must be at least 2 characters")]
    [property: MaxLength(2048, ErrorMessage = "Content cannot exceed 2048 characters")]
    string Content
);