using Cassandra;
using SharedModels;
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
        _session.Execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");
        _session.ChangeKeyspace("distcomp");
        _session.Execute(@"CREATE TABLE IF NOT EXISTS tbl_message (
            article_id bigint, id bigint, content text, state text, country text,
            PRIMARY KEY ((article_id), id));");
    }

    public IEnumerable<MessageResponseTo> GetAll()
    {
        var rows = _session.Execute("SELECT * FROM tbl_message ALLOW FILTERING");
        return rows.Select(MapRow);
    }

    public MessageResponseTo? GetById(long id)
    {
        var ps = _session.Prepare("SELECT * FROM tbl_message WHERE id = ? ALLOW FILTERING");
        var row = _session.Execute(ps.Bind(id)).FirstOrDefault();
        return row != null ? MapRow(row) : null;
    }

    public void Create(MessageResponseTo msg)
    {
        var ps = _session.Prepare("INSERT INTO tbl_message (article_id, id, content, state, country) VALUES (?, ?, ?, ?, ?)");
        _session.Execute(ps.Bind(msg.ArticleId, msg.Id, msg.Content, msg.State, "Unknown"));
    }

    public void Update(MessageResponseTo msg)
    {
        var ps = _session.Prepare("UPDATE tbl_message SET content = ?, state = ? WHERE article_id = ? AND id = ?");
        _session.Execute(ps.Bind(msg.Content, msg.State, msg.ArticleId, msg.Id));
    }

    public void Delete(long id, long articleId)
    {
        var ps = _session.Prepare("DELETE FROM tbl_message WHERE article_id = ? AND id = ?");
        _session.Execute(ps.Bind(articleId, id));
    }

    private MessageResponseTo MapRow(Row r) => new(
        r.GetValue<long>("id"), r.GetValue<long>("article_id"), 
        r.GetValue<string>("content"), r.GetValue<string>("state"));
}