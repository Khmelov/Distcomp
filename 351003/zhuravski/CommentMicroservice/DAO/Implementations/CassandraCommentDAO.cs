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
        context.Session.Execute(new SimpleStatement(@"
            CREATE TABLE IF NOT EXISTS distcomp.tbl_comments_by_article (
                article_id BIGINT,
                comment_id BIGINT,
                content TEXT,
                PRIMARY KEY (article_id, comment_id)
            )
        "));
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

        var id = model.Id;
        var articleId = model.ArticleId;
        var content = model.Content;

        await context.Session.ExecuteAsync(new SimpleStatement(
            "INSERT INTO distcomp.tbl_comments (id, article_id, content) VALUES (?, ?, ?)",
            id, articleId, content));

        await context.Session.ExecuteAsync(new SimpleStatement(
            "INSERT INTO distcomp.tbl_comments_by_article (article_id, comment_id, content) VALUES (?, ?, ?)",
            articleId, id, content));

        CommentModel result = model.Clone();
        return result;
    }

    public async Task DeleteAsync(long id)
    {
        var selectRs = await context.Session.ExecuteAsync(
            new SimpleStatement("SELECT article_id FROM distcomp.tbl_comments WHERE id = ?", id));
        
        var row = selectRs.FirstOrDefault();
        if (row == null)
        {
            throw new DAOObjectNotFoundException();
        }

        var articleId = row.GetValue<long>("article_id");

        await context.Session.ExecuteAsync(
            new SimpleStatement("DELETE FROM distcomp.tbl_comments WHERE id = ?", id));

        await context.Session.ExecuteAsync(
            new SimpleStatement("DELETE FROM distcomp.tbl_comments_by_article WHERE article_id = ? AND comment_id = ?", articleId, id));
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

        var stmt1 = new SimpleStatement(
            "UPDATE distcomp.tbl_comments SET article_id = ?, content = ? WHERE id = ?",
            model.ArticleId, model.Content, model.Id);

        var stmt2 = new SimpleStatement(
            "UPDATE distcomp.tbl_comments_by_article SET content = ? WHERE article_id = ? AND comment_id = ?",
            model.Content, model.ArticleId, model.Id);

        await context.Session.ExecuteAsync(stmt1);
        await context.Session.ExecuteAsync(stmt2);
        return model;
    }

    public async Task DeleteByArticleIdAsync(long articleId)
    {
        //Дай бог это никто не увидит.
        var stmt1 = new SimpleStatement("SELECT comment_id FROM distcomp.tbl_comments_by_article WHERE article_id = ?", articleId);
        var rs = await context.Session.ExecuteAsync(stmt1);
    
        var commentIds = rs.Select(r => r.GetValue<long>("comment_id")).ToList();

        foreach (var id in commentIds)
        {
            await context.Session.ExecuteAsync(
                new SimpleStatement("DELETE FROM distcomp.tbl_comments WHERE id = ?", id));
            await context.Session.ExecuteAsync(
                new SimpleStatement("DELETE FROM distcomp.tbl_comments_by_article WHERE article_id = ? AND comment_id = ?", articleId, id));
        }
    }
}