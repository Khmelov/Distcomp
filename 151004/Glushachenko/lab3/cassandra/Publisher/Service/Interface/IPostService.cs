using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Service.Interface.Common;

namespace Publisher.Service.Interface
{
    public interface IPostService : ICrudService<Post, PostRequestTO, PostResponseTO>
    {
        Task<IList<Post>> GetByTweetID(int tweetId);
    }
}
