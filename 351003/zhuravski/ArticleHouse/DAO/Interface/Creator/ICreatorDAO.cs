namespace ArticleHouse.DAO.CreatorDAO;

public interface ICreatorDAO
{
    Task<CreatorModel[]> GetAllCreators();
    Task<CreatorModel> AddNewCreator(CreatorModel model);
}