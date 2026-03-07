using Additions.DAO;
using Cassandra;
using CommentMicroservice.DAO.Interfaces;
using CommentMicroservice.DAO.Models;

namespace CommentMicroservice.DAO.Implementations;

class CassandraCommentDAO : ICommentDAO
{
    private readonly CassandraContext context;
    private readonly IArticleDAO articleDAO;

    public CassandraCommentDAO(CassandraContext context, IArticleDAO articleDAO)
    {
        this.context = context;
        this.articleDAO = articleDAO;
        context.Session.Execute(@"
            CREATE KEYSPACE IF NOT EXISTS distcomp 
            WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
        ");
        context.Session.Execute(@"
            CREATE TABLE IF NOT EXISTS distcomp.tbl_comments (
                id BIGINT PRIMARY KEY,
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
            Id = row.GetValue<long>("id"),
            ArticleId = row.GetValue<long>("article_id"),
            Content = row.GetValue<string>("content") ?? string.Empty
        }).ToArray();

        return comments;
    }

    public async Task<CommentModel> AddNewAsync(CommentModel model)
    {
        try
        {
            await articleDAO.GetByIdAsync(model.ArticleId);
        }
        catch (DAOException)
        {
            throw new DAOUpdateException("Mentioned article does not exist.");
        }

        model.Id = Random.Shared.NextInt64(1000000000, long.MaxValue);

        var stmt = new SimpleStatement(
            "INSERT INTO distcomp.tbl_comments (id, article_id, content) VALUES (?, ?, ?)",
            model.Id,
            model.ArticleId,
            model.Content
        );
        await context.Session.ExecuteAsync(stmt);
        return model;
    }

    public async Task DeleteAsync(long id)
    {
        var rs = await context.Session.ExecuteAsync(
            new SimpleStatement("DELETE FROM distcomp.tbl_comments WHERE id = ? IF EXISTS", id)
        );
        var row = rs.FirstOrDefault();
        if (row == null || !row.GetValue<bool>("[applied]"))
        {
            throw new DAOObjectNotFoundException();
        }
    }

    public async Task<CommentModel> GetByIdAsync(long id)
    {
        var rs = await context.Session.ExecuteAsync(new SimpleStatement(
            "SELECT id, article_id, content FROM distcomp.tbl_comments WHERE id = ?", id));

        var row = rs.FirstOrDefault();
        if (row == null) {
            throw new DAOObjectNotFoundException();
        };

        return new CommentModel
        {
            Id = row.GetValue<long>("id"),
            ArticleId = row.GetValue<long>("article_id"),
            Content = row.GetValue<string>("content") ?? string.Empty
        };
    }

    public async Task<CommentModel> UpdateAsync(CommentModel model)
    {
        try
        {
            await articleDAO.GetByIdAsync(model.ArticleId);
        }
        catch (DAOException)
        {
            throw new DAOUpdateException("Mentioned article does not exist.");
        }

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