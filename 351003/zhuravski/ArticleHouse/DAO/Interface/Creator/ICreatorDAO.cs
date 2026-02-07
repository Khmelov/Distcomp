namespace ArticleHouse.DAO.CreatorDAO;

public interface ICreatorDAO
{
    Task<CreatorModel[]> GetAllCreatorsAsync();
    Task<CreatorModel> AddNewCreatorAsync(CreatorModel model);
    Task DeleteCreatorAsync(long creatorId);
    Task<CreatorModel> GetCreatorByIdAsync(long creatorId);
}