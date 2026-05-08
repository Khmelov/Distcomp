using Cassandra;
using Discussion.Models;

namespace Discussion.Services;

public class CassandraService
{
    // ИСПРАВЛЕНИЕ: Явно указываем пространство имен Cassandra
    private readonly Cassandra.ISession _session;

    public CassandraService(IConfiguration config)
    {
        var cluster = Cluster.Builder()
            .AddContactPoint(config["Cassandra:ContactPoints"])
            .WithPort(int.Parse(config["Cassandra:Port"]!))
            .Build();
        
        _session = cluster.Connect();
        
        // Автоматически создаем Keyspace и таблицу, если их нет
        _session.Execute("CREATE KEYSPACE IF NOT EXISTS discussion_ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");
        _session.Execute("USE discussion_ks;");
        _session.Execute("CREATE TABLE IF NOT EXISTS tbl_post (id int PRIMARY KEY, newsid int, content text);");
    }

    public Post? GetPost(int id)
    {
        var row = _session.Execute($"SELECT id, newsid, content FROM tbl_post WHERE id = {id}").FirstOrDefault();
        if (row == null) return null;
        return new Post { Id = row.GetValue<int>("id"), NewsId = row.GetValue<int>("newsid"), Content = row.GetValue<string>("content") };
    }

    public void CreateOrUpdatePost(Post post)
    {
        var stmt = new SimpleStatement($"INSERT INTO tbl_post (id, newsid, content) VALUES (?, ?, ?)", post.Id, post.NewsId, post.Content);
        _session.Execute(stmt);
    }

    public void DeletePost(int id)
    {
        _session.Execute($"DELETE FROM tbl_post WHERE id = {id}");
    }
}