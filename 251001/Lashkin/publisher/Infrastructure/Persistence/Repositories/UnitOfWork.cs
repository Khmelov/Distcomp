using Domain.Interfaces;

namespace Persistence.Repositories;

public class UnitOfWork : IUnitOfWork
{
    private readonly RepositoryContext _context;
    private readonly Lazy<IUserRepository> _userRepository;
    private readonly Lazy<INewsRepository> _newsRepository;
    private readonly Lazy<INoticeRepository> _noticeRepository;
    private readonly Lazy<ILabelRepository> _labelRepository;
    
    public UnitOfWork(RepositoryContext context)
    {
        _context = context;
        _userRepository = new Lazy<IUserRepository>(() => new UserRepository(context));
        _newsRepository = new Lazy<INewsRepository>(() => new NewsRepository(context));
        _noticeRepository = new Lazy<INoticeRepository>(() => new NoticeRepository(context));
        _labelRepository = new Lazy<ILabelRepository>(() => new LabelRepository(context));
    }

    public IUserRepository User => _userRepository.Value;
    public INewsRepository News => _newsRepository.Value;
    public INoticeRepository Notice => _noticeRepository.Value;
    public ILabelRepository Label => _labelRepository.Value;

    public async Task SaveChangesAsync(CancellationToken cancellationToken = default) => await _context.SaveChangesAsync(cancellationToken);
}