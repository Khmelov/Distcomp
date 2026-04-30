using System.ComponentModel.DataAnnotations;

public class LabelRequestTo
{
    public long Id {get; set;}

    [StringLength(32, MinimumLength = 2)]
    public string Name {get; set;} = "";
}