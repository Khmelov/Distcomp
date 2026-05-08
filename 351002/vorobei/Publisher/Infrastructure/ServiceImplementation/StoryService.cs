using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Repositories;
using DataAccess.Models;
using Infrastructure.DatabaseContext;
using Infrastructure.Exceptions;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.ServiceImplementation
{
    public class StoryService : BaseService<Story, StoryRequestTo, StoryResponseTo>
    {
        private readonly IRepository<Creator> _creatorRepository;
        private readonly IRepository<Mark> _markRepository;
        private readonly DistcompContext _context;

        public StoryService(
            IRepository<Story> repository,
            IRepository<Creator> creatorRepository,
            IRepository<Mark> markRepository,
            DistcompContext context,
            IMapper mapper)
            : base(repository, mapper)
        {
            _creatorRepository = creatorRepository;
            _markRepository = markRepository;
            _context = context;
        }

        public async override Task<StoryResponseTo> CreateAsync(StoryRequestTo entity)
        {
            if (!await _creatorRepository.ExistsAsync(entity.CreatorId))
            {
                throw new BaseException(403, "Creator with such id does not exists");
            }

            var allStories = await _repository.GetAllAsync();
            var existingStory = allStories.FirstOrDefault(c => c.Title == entity.Title && c.CreatorId == entity.CreatorId);
            if (existingStory != null)
            {
                throw new BaseException(403, "Story with such title and creatorId already exists");
            }

            Story story = _mapper.Map<Story>(entity);
            story.Id = await _repository.GetLastIdAsync() + 1;
            story.Marks = new List<Mark>();

            var allExistingMarks = await _markRepository.GetAllAsync();

            foreach (var markName in entity.Marks)
            {
                var mark = allExistingMarks.FirstOrDefault(m => m.Name == markName);

                if (mark == null)
                {
                    mark = new Mark { Name = markName };
                    mark.Id = await _markRepository.GetLastIdAsync() + 1;
                    await _markRepository.CreateAsync(mark);
                }

                story.Marks.Add(mark);
            }

            await _repository.CreateAsync(story);

            return _mapper.Map<StoryResponseTo>(story);
        }
    }
}
