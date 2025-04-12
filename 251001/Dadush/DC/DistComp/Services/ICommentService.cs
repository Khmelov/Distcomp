using DistComp.Models;

namespace DistComp.Services {
    public interface ICommentService : ICommonAsyncService<long, Comment, CommentInDto, CommentOutDto> { }
}
