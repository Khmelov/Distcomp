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
        CREATE TABLE IF NOT EXISTS distcomp.comments (
            id UUID PRIMARY KEY,
            article_id UUID,
            content TEXT
        )
        ");
    }

    public async Task<CommentModel[]> GetAllAsync()
    {
        
    }
    public async Task<CommentModel> AddNewAsync(T model)
    {
        
    }
    public async Task DeleteAsync(long id)
    {
        
    }
    public async Task<CommentModel> GetByIdAsync(long id)
    {
        
    }
    public async Task<CommentModel> UpdateAsync(CommentModel model)
    {
        
    }
}