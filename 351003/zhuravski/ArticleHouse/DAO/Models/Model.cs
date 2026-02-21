namespace ArticleHouse.DAO.Models;

public abstract class Model<T> where T : Model<T>
{
    public long Id {get; set;}
    public virtual T Clone()
    {
        return (T)MemberwiseClone();
    }
}