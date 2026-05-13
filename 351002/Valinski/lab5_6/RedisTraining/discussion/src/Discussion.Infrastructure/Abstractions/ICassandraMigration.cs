using Cassandra;

namespace Discussion.Infrastructure.Abstractions;

public interface ICassandraMigration
{
    string Id { get; } 
    Task UpAsync(ISession session);
}
