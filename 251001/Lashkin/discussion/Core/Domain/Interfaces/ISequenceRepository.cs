namespace Domain.Interfaces;

public interface ISequenceRepository
{
    Task<long> GetNextIdAsync(string collectionName);
}