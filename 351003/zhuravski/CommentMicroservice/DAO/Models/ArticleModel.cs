using Additions.DAO;

namespace CommentMicroservice.DAO.Models;

public class ArticleModel : LongIdModel<ArticleModel>
{
    public long CreatorId {get; set;}
    public string Title {get; set;} = default!;
    public string Content {get; set;} = default!;
    //Отметки времени пока решено пропустить.
}