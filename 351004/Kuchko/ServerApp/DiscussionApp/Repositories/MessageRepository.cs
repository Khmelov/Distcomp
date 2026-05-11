using Cassandra;
using ISession = Cassandra.ISession;

namespace DiscussionApp.Repositories;

public class MessageRepository
{
    private readonly ISession _session;

    public MessageRepository(IConfiguration config)
    {
        var contactPoint = config["Cassandra:ContactPoint"] ?? "localhost";
        var cluster = Cluster.Builder().AddContactPoint(contactPoint).Build();
        _session = cluster.Connect();

        InitializeDatabase();
    }

    private void InitializeDatabase()
    {
        // Создаем Keyspace (Схему distcomp)
        _session.Execute(@"
            CREATE KEYSPACE IF NOT EXISTS distcomp 
            WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");

        _session.ChangeKeyspace("distcomp");

        // Создаем таблицу. РЕШАЕМ ПРОБЛЕМУ ПЕРЕКОСА ДАННЫХ:
        // PRIMARY KEY ((article_id), id) - article_id это partition key!
        _session.Execute(@"
            CREATE TABLE IF NOT EXISTS tbl_message (
                article_id bigint,
                id bigint,
                country text,
                content text,
                PRIMARY KEY ((article_id), id)
            );");
    }

    public void Create(long articleId, long id, string content, string country = "Unknown")
    {
        var ps = _session.Prepare("INSERT INTO tbl_message (article_id, id, country, content) VALUES (?, ?, ?, ?)");
        _session.Execute(ps.Bind(articleId, id, country, content));
    }

    // Для внешнего API
    public IEnumerable<dynamic> GetByArticle(long articleId)
    {
        var ps = _session.Prepare("SELECT * FROM tbl_message WHERE article_id = ?");
        var rows = _session.Execute(ps.Bind(articleId));
        return rows.Select(r => new {
            Id = r.GetValue<long>("id"),
            ArticleId = r.GetValue<long>("article_id"),
            Content = r.GetValue<string>("content")
        });
    }
}