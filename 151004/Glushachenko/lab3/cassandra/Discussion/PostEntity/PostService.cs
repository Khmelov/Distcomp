using AutoMapper;
using Discussion.Common;
using Discussion.PostEntity.Interface;

namespace Discussion.PostEntity
{
    public class PostService(IMapper mapper, IPostRepository repository)
        : AbstractCrudService<Post, PostRequestTO, PostResponseTO>(mapper, repository), IPostService
    {
        public override async Task<PostResponseTO> Add(PostRequestTO post)
        {
            if (!Validate(post))
            {
                throw new InvalidDataException("POST is not valid");
            }

            return await base.Add(post);
        }

        public override async Task<PostResponseTO> Update(PostRequestTO post)
        {
            if (!Validate(post))
            {
                throw new InvalidDataException($"UPDATE invalid data: {post}");
            }

            return await base.Update(post);
        }

        public Task<IList<Post>> GetByTweetID(int tweetId)
        {
            throw new NotImplementedException();
        }

        private static bool Validate(PostRequestTO post)
        {
            var contentLen = post.Content.Length;

            if (contentLen < 2 || contentLen > 2048)
            {
                return false;
            }
            return true;
        }
    }
}
