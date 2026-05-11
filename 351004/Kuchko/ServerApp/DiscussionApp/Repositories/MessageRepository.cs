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

    public IEnumerable<dynamic> GetAll()
    {
        // Внимание: В Cassandra ALLOW FILTERING или SELECT * без ключа 
        // считается плохой практикой на больших объемах данных, но для тестов ТЗ это нужно.
        var ps = _session.Prepare("SELECT * FROM tbl_message");
        var rows = _session.Execute(ps.Bind());
        
        return rows.Select(r => new {
            Id = r.GetValue<long>("id"),
            ArticleId = r.GetValue<long>("article_id"),
            // Добавляем Country, так как ТЗ (схема) требует его возвращать
            Country = r.GetValue<string>("country"),
            Content = r.GetValue<string>("content")
        });
    }
    
    public dynamic? GetById(long id)
    {
        // ВАЖНО: В Cassandra фильтрация только по clustering key (id) без partition key (article_id)
        // требует ALLOW FILTERING. Это плохо для продакшена, но допустимо для учебного ТЗ.
        var ps = _session.Prepare("SELECT * FROM tbl_message WHERE id = ? ALLOW FILTERING");
        var row = _session.Execute(ps.Bind(id)).FirstOrDefault();
        
        if (row == null) return null;

        return new {
            Id = row.GetValue<long>("id"),
            ArticleId = row.GetValue<long>("article_id"),
            Country = row.GetValue<string>("country"),
            Content = row.GetValue<string>("content")
        };
    }

    public void Update(long id, long articleId, string content, string country = "Unknown")
    {
        // Для UPDATE в Cassandra обязательно нужен весь PRIMARY KEY (article_id, id)
        var ps = _session.Prepare("UPDATE tbl_message SET content = ?, country = ? WHERE article_id = ? AND id = ?");
        _session.Execute(ps.Bind(content, country, articleId, id));
    }

    public void Delete(long id, long articleId)
    {
        var ps = _session.Prepare("DELETE FROM tbl_message WHERE article_id = ? AND id = ?");
        _session.Execute(ps.Bind(articleId, id));
    }
}