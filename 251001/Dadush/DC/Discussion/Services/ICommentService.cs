using Discussion.Models;

namespace Discussion.Services {
    public interface ICommentService : ICommonAsyncService<long, Comment, CommentInDto, CommentOutDto> {
    }
}
