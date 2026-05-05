using System.ComponentModel.DataAnnotations;

public class CommentRequestTo
{
    public long Id {get; set;}
    public long StoryId {get; set;}

    [StringLength(2048, MinimumLength = 2)]
    public string Content {get; set;} = "";
}