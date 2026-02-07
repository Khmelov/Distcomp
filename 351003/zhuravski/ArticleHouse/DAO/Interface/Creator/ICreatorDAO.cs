namespace ArticleHouse.DAO.CreatorDAO;

public interface ICreatorDAO
{
    Task<CreatorModel[]> GetAllCreators();
    //CreatorModel AddNewCreator();
}