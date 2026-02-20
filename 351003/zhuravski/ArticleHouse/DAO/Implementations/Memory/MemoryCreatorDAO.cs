using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Implementations.Memory;
//В следующей лабе разберусь с этой жёсткой связью.
public class MemoryCreatorDAO : ICreatorDAO
{
    private readonly MemoryDAO<CreatorModel> memoryDAO = new();

    public Task<CreatorModel[]> GetAllAsync() => memoryDAO.GetAllAsync();
    public Task<CreatorModel> AddNewAsync(CreatorModel model) => memoryDAO.AddNewAsync(model);
    public Task DeleteAsync(long id) => memoryDAO.DeleteAsync(id);
    public Task<CreatorModel> GetByIdAsync(long id) => memoryDAO.GetByIdAsync(id);
    public Task<CreatorModel> UpdateAsync(CreatorModel model) => memoryDAO.UpdateAsync(model);
}