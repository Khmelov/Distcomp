using Riok.Mapperly.Abstractions;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Models;

namespace TaskREST.Mappers;

[Mapper]
public static partial class PostMapper
{
    public static partial Post Map(UpdatePostRequestTo updatePostRequestTo);
    public static partial Post Map(CreatePostRequestTo createPostRequestTo);
    public static partial PostResponseTo Map(Post post);
    public static partial IEnumerable<PostResponseTo> Map(IEnumerable<Post> posts);

    public static partial IEnumerable<Post> Map(
        IEnumerable<UpdatePostRequestTo> postRequestTos);
}