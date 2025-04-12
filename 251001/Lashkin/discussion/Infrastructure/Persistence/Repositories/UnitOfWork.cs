using Domain.Interfaces;
using MongoDB.Driver;

namespace Persistence.Repositories;

public class UnitOfWork : IUnitOfWork
{
    private readonly Lazy<INoticeRepository> _noticeRepository;
    private readonly Lazy<ISequenceRepository> _sequenceRepository;

    public UnitOfWork(IMongoDatabase database)
    {
        _noticeRepository = new Lazy<INoticeRepository>(() => new NoticeRepository(database, "notices"));
        _sequenceRepository = new Lazy<ISequenceRepository>(() => new SequenceRepository(database));
    }

    public INoticeRepository Notice => _noticeRepository.Value;
    public ISequenceRepository Sequence => _sequenceRepository.Value;
}
