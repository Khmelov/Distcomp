using DiscussionService.Models;
using MongoDB.Driver;

namespace DiscussionService.Repositories
{
    public class MongoContext
    {
        private readonly IMongoDatabase _database;

        public MongoContext(IConfiguration configuration)
        {
            var client = new MongoClient(configuration["MongoDbSettings:ConnectionString"]);

            _database = client.GetDatabase(configuration["MongoDbSettings:DatabaseName"]);
        }

        public IMongoCollection<Post> Posts => _database.GetCollection<Post>("posts");
    }
}