using Riok.Mapperly.Abstractions;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Models;

namespace TaskREST.Mappers;

[Mapper]
public static partial class TagMapper
{
    public static partial Tag Map(UpdateTagRequestTo updateTagRequestTo);
    public static partial Tag Map(CreateTagRequestTo createTagRequestTo);
    public static partial TagResponseTo Map(Tag tag);
    public static partial IEnumerable<TagResponseTo> Map(IEnumerable<Tag> tags);

    public static partial IEnumerable<Tag> Map(
        IEnumerable<UpdateTagRequestTo> tagRequestTos);
}