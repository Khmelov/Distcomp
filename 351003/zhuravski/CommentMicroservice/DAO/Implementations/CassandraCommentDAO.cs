using Additions.DAO;
using Cassandra;
using CommentMicroservice.DAO.Interfaces;
using CommentMicroservice.DAO.Models;

namespace CommentMicroservice.DAO.Implementations;

class CassandraCommentDAO : ICommentDAO
{
    private readonly CassandraContext context;

    public CassandraCommentDAO(CassandraContext context)
    {
        this.context = context;
        context.Session.Execute(@"
            CREATE KEYSPACE IF NOT EXISTS distcomp 
            WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
        ");
        context.Session.Execute(@"
            CREATE TABLE IF NOT EXISTS distcomp.tbl_comments (
                id UUID PRIMARY KEY,
                article_id BIGINT,
                content TEXT
            )
        ");
    }

    public async Task<CommentModel[]> GetAllAsync()
    {
        var rs = await context.Session.ExecuteAsync(new SimpleStatement(
            "SELECT id, article_id, content FROM distcomp.tbl_comments"));
        var comments = rs.Select(row => new CommentModel
        {
            Id = row.GetValue<Guid>("id"),
            ArticleId = row.GetValue<long>("article_id"),
            Content = row.GetValue<string>("content") ?? string.Empty
        }).ToArray();

        return comments;
    }

    public async Task<CommentModel> AddNewAsync(CommentModel model)
    {
        if (model.Id == Guid.Empty)
            model.Id = Guid.NewGuid();

        var stmt = new SimpleStatement(
            "INSERT INTO distcomp.tbl_comments (id, article_id, content) VALUES (?, ?, ?)",
            model.Id,
            model.ArticleId,
            model.Content
        );
        await context.Session.ExecuteAsync(stmt);
        return model;
    }

    public async Task DeleteAsync(Guid id)
    {
        await context.Session.ExecuteAsync(new SimpleStatement(
            "DELETE FROM distcomp.tbl_comments WHERE id = ?", id));
    }

    public async Task<CommentModel> GetByIdAsync(Guid id)
    {
        var rs = await context.Session.ExecuteAsync(new SimpleStatement(
            "SELECT id, article_id, content FROM distcomp.tbl_comments WHERE id = ?", id));

        var row = rs.FirstOrDefault();
        if (row == null) {
            throw new DAOObjectNotFoundException();
        };

        return new CommentModel
        {
            Id = row.GetValue<Guid>("id"),
            ArticleId = row.GetValue<long>("article_id"),
            Content = row.GetValue<string>("content") ?? string.Empty
        };
    }

    public async Task<CommentModel> UpdateAsync(CommentModel model)
    {
        var stmt = new SimpleStatement(
            "UPDATE distcomp.tbl_comments SET article_id = ?, content = ? WHERE id = ?",
            model.ArticleId,
            model.Content,
            model.Id
        );
        await context.Session.ExecuteAsync(stmt);
        return model;
    }
}