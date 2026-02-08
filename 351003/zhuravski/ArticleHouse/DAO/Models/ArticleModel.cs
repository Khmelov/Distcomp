namespace ArticleHouse.DAO.Models;

public class ArticleModel : Model<ArticleModel>
{
    public long CreatorId {get; set;}
    public string Title {get; set;} = default!;
    public string Content {get; set;} = default!;
    //Отметки времени пока решено пропустить.
}