using Domain.Entities;
using Domain.Interfaces;
using MongoDB.Driver;

namespace Persistence.Repositories;

public class SequenceRepository : ISequenceRepository
{
    private readonly IMongoCollection<Sequence> _collection;

    public SequenceRepository(IMongoDatabase database)
    {
        _collection = database.GetCollection<Sequence>("sequences");
    }

    public async Task<long> GetNextIdAsync(string collectionName)
    {
        var filter = Builders<Sequence>.Filter.Eq(s => s.CollectionName, collectionName);
        var update = Builders<Sequence>.Update.Inc(s => s.CurrentId, 1);
        var options = new FindOneAndUpdateOptions<Sequence>()
        {
            ReturnDocument = ReturnDocument.After,
            IsUpsert = true
        };
        var counter = await _collection.FindOneAndUpdateAsync(filter, update, options);
        
        return counter.CurrentId;
    }
}