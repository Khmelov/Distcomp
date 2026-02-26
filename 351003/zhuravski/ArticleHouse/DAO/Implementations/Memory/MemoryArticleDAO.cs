using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Implementations.Memory;
//В следующей лабе разберусь с этой жёсткой связью.
class MemoryArticleDAO : IArticleDAO
{
    private readonly MemoryDAO<ArticleModel> memoryDAO = new();

    public Task<ArticleModel[]> GetAllAsync() => memoryDAO.GetAllAsync();
    public Task<ArticleModel> AddNewAsync(ArticleModel model) => memoryDAO.AddNewAsync(model);
    public Task DeleteAsync(long id) => memoryDAO.DeleteAsync(id);
    public Task<ArticleModel> GetByIdAsync(long id) => memoryDAO.GetByIdAsync(id);
    public Task<ArticleModel> UpdateAsync(ArticleModel model) => memoryDAO.UpdateAsync(model);
}