using System.ComponentModel.DataAnnotations;

public class StoryRequestTo
{
    public long Id {get; set;}
    public long WriterId {get; set;}
 
    [StringLength(64, MinimumLength = 2)]
    public string Title {get; set;} = "";
 
    [StringLength(2048, MinimumLength = 4)]
    public string Content {get; set;}= "";
}