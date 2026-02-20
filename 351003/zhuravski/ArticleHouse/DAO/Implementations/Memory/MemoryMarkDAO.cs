using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Implementations.Memory;
//В следующей лабе разберусь с этой жёсткой связью.
public class MemoryMarkDAO : IMarkDAO
{
    private readonly MemoryDAO<MarkModel> memoryDAO = new();

    public Task<MarkModel[]> GetAllAsync() => memoryDAO.GetAllAsync();
    public Task<MarkModel> AddNewAsync(MarkModel model) => memoryDAO.AddNewAsync(model);
    public Task DeleteAsync(long id) => memoryDAO.DeleteAsync(id);
    public Task<MarkModel> GetByIdAsync(long id) => memoryDAO.GetByIdAsync(id);
    public Task<MarkModel> UpdateAsync(MarkModel model) => memoryDAO.UpdateAsync(model);
}