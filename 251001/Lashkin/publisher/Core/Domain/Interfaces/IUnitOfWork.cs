namespace Domain.Interfaces;

public interface IUnitOfWork
{
    IUserRepository User { get; }
    INewsRepository News { get; }
    INoticeRepository Notice { get; }
    ILabelRepository Label { get; }
    Task SaveChangesAsync(CancellationToken cancellationToken = default);
}