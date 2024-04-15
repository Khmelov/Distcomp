using AutoMapper;
using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Repository.Interface;
using Publisher.Service.Implementation.Common;
using Publisher.Service.Interface;

namespace Publisher.Service.Implementation
{
    public class AuthorService(IMapper mapper, IAuthorRepository repository) :
        AbstractCrudService<Author, AuthorRequestTO, AuthorResponseTO>(mapper, repository), IAuthorService
    {
        private readonly IMapper _mapper = mapper;

        public override async Task<AuthorResponseTO> Add(AuthorRequestTO authorTo)
        {
            if (!Validate(authorTo))
            {
                throw new InvalidDataException("Author is not valid");
            }

            return await base.Add(authorTo);
        }

        public override async Task<AuthorResponseTO> Update(AuthorRequestTO authorTo)
        {
            if (!Validate(authorTo))
            {
                throw new InvalidDataException($"UPDATE invalid data: {authorTo}");
            }

            return await base.Update(authorTo);
        }

        public async Task<AuthorResponseTO> GetByTweetID(int tweetId)
        {
            var response = await repository.GetByTweetIdAsync(tweetId);

            return _mapper.Map<AuthorResponseTO>(response.Author);
        }

        private static bool Validate(AuthorRequestTO author)
        {
            var fnameLen = author.FirstName.Length;
            var lnameLen = author.LastName.Length;
            var passLen = author.Password.Length;
            var loginLen = author.Login.Length;

            if (fnameLen < 2 || fnameLen > 64)
            {
                return false;
            }
            if (lnameLen < 2 || fnameLen > 64)
            {
                return false;
            }
            if (passLen < 8 || passLen > 128)
            {
                return false;
            }
            if (loginLen < 2 || loginLen > 64)
            {
                return false;
            }
            return true;
        }
    }
}
