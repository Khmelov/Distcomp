using Discussion.Models;

namespace Discussion.Data {
    public interface ICommentRepository : IRepository<long, Comment> { }
}
