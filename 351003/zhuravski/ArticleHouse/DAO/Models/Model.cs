namespace ArticleHouse.DAO.Models;

public abstract class Model
{
    public long Id {get; set;}
    public abstract Model Clone();
}