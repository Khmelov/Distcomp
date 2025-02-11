using System.Runtime.Serialization;

namespace Application.DTO.Response.Post;

[Serializable]
[DataContract(Name="post")]
public class PostResponseToGetById
{
    public long Id { get; init; }
    public long NewsId { get; init; }
    public string Content { get; init; }
}