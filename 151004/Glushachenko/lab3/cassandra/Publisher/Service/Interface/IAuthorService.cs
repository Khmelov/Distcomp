using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Service.Interface.Common;

namespace Publisher.Service.Interface
{
    public interface IAuthorService : ICrudService<Author, AuthorRequestTO, AuthorResponseTO>
    {
        Task<AuthorResponseTO> GetByTweetID(int tweetId);
    }
}
