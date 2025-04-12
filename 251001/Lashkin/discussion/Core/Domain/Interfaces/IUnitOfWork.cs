namespace Domain.Interfaces;

public interface IUnitOfWork
{
    INoticeRepository Notice { get; }
    ISequenceRepository Sequence { get; }
}