using Cassandra;
using Cassandra.Mapping;
using RestApiTask.Models.Entities;

namespace RestApiTask.Repositories;

public class CassandraRepository : IRepository<Message>
{
    private readonly IMapper _mapper;
    private readonly Cassandra.ISession _session;

    public CassandraRepository()
    {
        var cluster = Cluster.Builder()
            .AddContactPoint("127.0.0.1")
            .Build();

        using var session = cluster.Connect();

        session.Execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

        _session = cluster.Connect("distcomp");
        _mapper = new Mapper(_session);
    }

    public async Task<IEnumerable<Message>> GetAllAsync() => await _mapper.FetchAsync<Message>();

    public async Task<PagedResult<Message>> GetAllAsync(QueryOptions options)
    {
        // Упрощенная реализация пагинации для Cassandra
        var items = (await _mapper.FetchAsync<Message>()).ToList();
        return new PagedResult<Message>(items, items.Count, options.PageNumber, options.PageSize);
    }

    public async Task<Message?> GetByIdAsync(long id) =>
        (await _mapper.FetchAsync<Message>("WHERE id = ?", id)).FirstOrDefault();

    public async Task<Message> AddAsync(Message entity)
    {
        if (entity.Id <= 0) entity.Id = DateTime.UtcNow.Ticks; // Генерация ID
        await _mapper.InsertAsync(entity);
        return entity;
    }

    public async Task<Message> UpdateAsync(Message entity)
    {
        await _mapper.UpdateAsync(entity);
        return entity;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        await _session.ExecuteAsync(new SimpleStatement("DELETE FROM tbl_message WHERE id = ?", id));
        return true;
    }
}