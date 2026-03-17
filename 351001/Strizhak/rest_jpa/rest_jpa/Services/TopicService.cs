using AutoMapper;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Repositories;
using System;
using System.Threading.Tasks;

namespace rest_api.Services
{
    public class TopicService : BaseService<Topic, TopicRequestTo, TopicResponseTo>
    {
        public TopicService(IRepository<Topic> repository, IMapper mapper)
            : base(repository, mapper)
        {
        }

        public override async Task<TopicResponseTo> CreateAsync(TopicRequestTo request)
        {
            var topic = _mapper.Map<Topic>(request);
            topic.Created = DateTime.UtcNow;
            topic.Modified = DateTime.UtcNow;

            await _repository.AddAsync(topic);
            await _repository.SaveChangesAsync();

            return _mapper.Map<TopicResponseTo>(topic);
        }

        public override async Task<TopicResponseTo> UpdateAsync(long id, TopicRequestTo request)
        {
           
            var existingTopic = await _repository.GetByIdAsync(id);
            if (existingTopic == null)
                throw new KeyNotFoundException($"Topic with id {id} not found");

            _mapper.Map(request, existingTopic);
            existingTopic.Modified = DateTime.UtcNow; 

            _repository.Update(existingTopic);
            await _repository.SaveChangesAsync();

            return _mapper.Map<TopicResponseTo>(existingTopic);
        }
    }
}