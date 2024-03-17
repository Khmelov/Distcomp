using Riok.Mapperly.Abstractions;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Models;

namespace TaskREST.Mappers;

[Mapper]
public static partial class CreatorMapper
{
    public static partial Creator Map(UpdateCreatorRequestTo updateCreatorRequestTo);
    public static partial Creator Map(CreateCreatorRequestTo createCreatorRequestTo);
    public static partial CreatorResponseTo Map(Creator creator);
    public static partial IEnumerable<CreatorResponseTo> Map(IEnumerable<Creator> creators);

    public static partial IEnumerable<Creator> Map(
        IEnumerable<UpdateCreatorRequestTo> creatorRequestTos);
}