namespace Domain.Repositories;

public interface IUnitOfWork
{
    IUserRepository User { get; }
    INewsRepository News { get; }
    INoticeRepository Notice { get; }
    ILabelRepository Label { get; }
    Task SaveChangesAsync();
    Task BeginTransactionAsync();
    Task CommitAsync();
    Task RollbackAsync();
}