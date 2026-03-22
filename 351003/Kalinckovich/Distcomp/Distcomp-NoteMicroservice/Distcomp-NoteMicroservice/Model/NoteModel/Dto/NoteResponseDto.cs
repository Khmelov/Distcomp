

namespace Distcomp_NoteMicroservice.Model.NoteModel.Dto;

public record NoteResponseDto(
    string Country,
    long TopicId,
    long Id,
    string Content,
    DateTimeOffset CreatedAt,
    DateTimeOffset? UpdatedAt
);