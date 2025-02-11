using System.Runtime.Serialization;

namespace Application.DTO.Response.Editor;

[Serializable]
[DataContract(Name="editor")]
public class EditorResponseToGetById
{
    public long Id { get; init; }
    public string Login { get; init; }
    public string Firstname { get; init; }
    public string? Lastname { get; init; }
}