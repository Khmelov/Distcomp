using DiscussionService.Interfaces;
using DiscussionService.Models;
using MongoDB.Bson;
using MongoDB.Driver;
using System.Diagnostics.Metrics;

namespace DiscussionService.Repositories
{
    public class PostRepository : IPostRepository
    {
        private readonly IMongoCollection<BsonDocument> _counters;

        private readonly IMongoCollection<Post> _collection;

        public PostRepository(IMongoDatabase database)
        {
            _collection = database.GetCollection<Post>("posts");
            _counters = database.GetCollection<BsonDocument>("counters");
        }

        public async Task<Post> AddAsync(Post post)
        {
            post.Id = await GetNextId();
            await _collection.InsertOneAsync(post);
            return post;
        }

        public async Task<Post?> GetByIdAsync(long id)
        {
            return await _collection.Find(p => p.Id == id).FirstOrDefaultAsync();
        }

        public async Task<List<Post>> GetAllAsync()
        {
            return await _collection.Find(_ => true).ToListAsync();
        }

        public async Task<Post?> UpdateAsync(Post post)
        {
            var result = await _collection.ReplaceOneAsync(p => p.Id == post.Id, post);

            if (result.MatchedCount == 0)
                return null;

            return post;
        }

        public async Task DeleteAsync(Post post)
        {
            var result = await _collection.DeleteOneAsync(p => p.Id == post.Id);
        }

        public async Task<long> GetNextId()
        {
            var filter = Builders<BsonDocument>.Filter.Eq("_id", "postId");

            var update = Builders<BsonDocument>.Update.Inc("value", 1);

            var options = new FindOneAndUpdateOptions<BsonDocument>
            {
                ReturnDocument = ReturnDocument.After,
                IsUpsert = true
            };

            var result = await _counters.FindOneAndUpdateAsync(filter, update, options);

            return result["value"].ToInt64(); ;
        }
    }
}
