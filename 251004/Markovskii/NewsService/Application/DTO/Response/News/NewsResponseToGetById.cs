using System.Runtime.Serialization;

namespace Application.DTO.Response.News;

[Serializable]
[DataContract(Name="news")]
public class NewsResponseToGetById
{
    public long Id { get; init; }
    public long EditorId { get; init; }
    public string Title { get; init; }
    public string Content { get; init; }
}