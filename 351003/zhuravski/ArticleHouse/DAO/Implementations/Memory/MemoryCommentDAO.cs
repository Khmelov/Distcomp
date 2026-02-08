using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Implementations.Memory;
//В следующей лабе разберусь с этой жёсткой связью.
class MemoryCommentDAO : ICommentDAO
{
    private readonly MemoryDAO<CommentModel> memoryDAO = new();

    public Task<CommentModel[]> GetAllAsync() => memoryDAO.GetAllAsync();
    public Task<CommentModel> AddNewAsync(CommentModel model) => memoryDAO.AddNewAsync(model);
    public Task DeleteAsync(long id) => memoryDAO.DeleteAsync(id);
    public Task<CommentModel> GetByIdAsync(long id) => memoryDAO.GetByIdAsync(id);
    public Task<CommentModel> UpdateAsync(CommentModel model) => memoryDAO.UpdateAsync(model);
}