namespace ArticleHouse.DAO.CreatorDAO;

public interface ICreatorDAO
{
    Task<CreatorModel[]> GetAllCreatorsAsync();
    Task<CreatorModel> AddNewCreatorAsync(CreatorModel model);
}