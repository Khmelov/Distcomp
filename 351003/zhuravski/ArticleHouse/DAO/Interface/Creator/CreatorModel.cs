namespace ArticleHouse.DAO.CreatorDAO;

public class CreatorModel
{
    public long Id {get; set;}
    public string Password {get; set;} = default!;
    public string Login {get; set;} = default!;
    public string FirstName {get; set;} = default!;
    public string LastName {get; set;} = default!;
};