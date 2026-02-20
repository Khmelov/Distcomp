namespace ArticleHouse.DAO.Models;

public class CreatorModel : Model<CreatorModel>
{
    public CreatorModel() {}
    public string Password {get; set;} = default!;
    public string Login {get; set;} = default!;
    public string FirstName {get; set;} = default!;
    public string LastName {get; set;} = default!;
};