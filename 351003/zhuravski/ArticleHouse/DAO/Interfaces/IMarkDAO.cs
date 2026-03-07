
using Additions.DAO;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Interfaces;

public interface IMarkDAO : IBasicDAO<MarkModel>
{
    Task ReleaseByIdsAsync(long[] ids);
    Task<long[]> ReserveIdsByNamesAsync(string[] names);
}