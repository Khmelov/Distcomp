using Discussion.Models;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace Discussion.Repositories;

public class NoticeRepository : INoticeRepository
{
    private readonly IMongoCollection<Notice> _notices;

    public NoticeRepository(IMongoClient client, IOptions<MongoDbSettings> settings)
    {
        var database = client.GetDatabase(settings.Value.DatabaseName);
        _notices = database.GetCollection<Notice>(settings.Value.CollectionName);
    }

    public async Task<List<Notice>> GetAllAsync() =>
        await _notices.Find(_ => true).ToListAsync();

    public async Task<Notice> GetByIdAsync(int id) =>
        await _notices.Find(n => n.Id == id).FirstOrDefaultAsync();

    public async Task CreateAsync(Notice notice) =>
        await _notices.InsertOneAsync(notice);

    public async Task UpdateAsync(int id, Notice notice) =>
        await _notices.ReplaceOneAsync(n => n.Id == id, notice);

    public async Task DeleteAsync(int id) =>
        await _notices.DeleteOneAsync(n => n.Id == id);
}