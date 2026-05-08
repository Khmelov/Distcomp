using Cassandra;
using Cassandra.Mapping;
using Discussion.Models;

namespace Discussion.Repositories
{
    public interface ICommentRepository
    {
        Task<IEnumerable<Comment>> GetAllAsync();
        Task<Comment?> GetByIdAsync(long id);
        Task<Comment> CreateAsync(Comment comment);
        Task<Comment> UpdateAsync(Comment comment);
        Task<bool> DeleteAsync(long id);
    }

    public class CommentRepository : ICommentRepository
    {
        private readonly Cassandra.ISession _session;
        private readonly IMapper _mapper;

        public CommentRepository()
        {
            var cluster = Cluster.Builder()
                .AddContactPoint("127.0.0.1")
                .WithPort(9042)
                .Build();

            _session = cluster.Connect("distcomp");
            _mapper = new Mapper(_session);
        }

        public async Task<IEnumerable<Comment>> GetAllAsync()
        {
            return await _mapper.FetchAsync<Comment>();
        }

        public async Task<Comment?> GetByIdAsync(long id)
        {
            var result = await _mapper.FetchAsync<Comment>("WHERE id = ?", id);
            return result.FirstOrDefault();
        }

        public async Task<Comment> CreateAsync(Comment comment)
        {
            if (comment.Id == 0)
            {
                comment.Id = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            }
            await _mapper.InsertAsync(comment);
            return comment;
        }

        public async Task<Comment> UpdateAsync(Comment comment)
        {
           //await _mapper.UpdateAsync(comment);
           await _mapper.InsertAsync(comment);
            return comment;
        }

        public async Task<bool> DeleteAsync(long id)
        {
            await _session.ExecuteAsync(
                new SimpleStatement("DELETE FROM tbl_comment WHERE id = ?", id)
            );
            return true;
        }
    }
}