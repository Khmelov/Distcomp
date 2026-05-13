using ArticleHouse.DAO.Models;
using Additions.DAO;

namespace ArticleHouse.DAO.Interfaces;

public interface IMarkDAO : ILongIdDAO<MarkModel>
{
    Task ReleaseByIdsAsync(long[] ids);
    Task<long[]> ReserveIdsByNamesAsync(string[] names);
}