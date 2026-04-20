using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Repositories;
using DataAccess.Models;
using Infrastructure.Exceptions;

namespace Infrastructure.ServiceImplementation
{
    public class PostService : BaseService<Post, PostRequestTo, PostResponseTo>
    {
        private readonly IRepository<Story> _storyRepository;

        public PostService(IRepository<Post> repository, IRepository<Story> storyRepository, IMapper mapper)
            : base(repository, mapper)
        {
            _storyRepository = storyRepository;
        }

        public async override Task<PostResponseTo> CreateAsync(PostRequestTo entity)
        {
            if (!await _storyRepository.ExistsAsync(entity.StoryId))
            {
                throw new BaseException(403, "Story with such id does not exists");
            }

            Post post = _mapper.Map<Post>(entity);
            post.Id = await _repository.GetLastIdAsync() + 1;

            await _repository.CreateAsync(post);
            return _mapper.Map<PostResponseTo>(post);
        }
    }
}
