using ArticleHouse.DAO.Models;
using Additions.DAO;

namespace ArticleHouse.DAO.Interfaces;

public interface ICreatorDAO : ILongIdDAO<CreatorModel>
{
    Task<CreatorModel> GetByLoginAsync(string login);
}