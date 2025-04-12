using Domain.Entities;
using Domain.Interfaces;
using MongoDB.Driver;

namespace Persistence.Repositories;

public class NoticeRepository : RepositoryBase<Notice>, INoticeRepository
{
    public NoticeRepository(IMongoDatabase database, string collectionName) : base(database, collectionName)
    {
    }
}