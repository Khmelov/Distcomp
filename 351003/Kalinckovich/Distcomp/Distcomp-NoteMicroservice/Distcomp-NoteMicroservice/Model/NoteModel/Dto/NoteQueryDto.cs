namespace Distcomp_NoteMicroservice.Model.NoteModel.Dto;

public record NoteQueryDto(
    string Country,
    long TopicId,
    long? Id = null
);