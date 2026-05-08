using rest_api.Entities;
using rest_api.InMemory;
using rest_api.Dtos;

namespace rest_api.Services
{
    public class TopicService : BaseService<Topic, TopicRequestTo, TopicResponseTo>
    {
        public TopicService(IRepository<Topic> repository) : base(repository)
        {
        }

        public override TopicResponseTo Create(TopicRequestTo request)
        {
            var topic = MapToEntity(request);
            topic.Created = DateTime.UtcNow;
            topic.Modified = DateTime.UtcNow;

            _repository.Add(topic);
            return MapToResponse(topic);
        }

        public override TopicResponseTo Update(TopicRequestTo request)
        {
            var id = request.Id;
            var topic = _repository.GetById(id);
            if (topic == null)
                throw new KeyNotFoundException($"Topic with id {id} not found");

            topic.UserId = request.UserId;
            topic.Title = request.Title;
            topic.Content = request.Content;
            topic.Modified = DateTime.UtcNow;

            _repository.Update(topic);
            return MapToResponse(topic);
        }

        protected override TopicResponseTo MapToResponse(Topic entity)
        {
            return new TopicResponseTo
            {
                Id = entity.Id,
                UserId = entity.UserId,
                Title = entity.Title,
                Content = entity.Content,
                Created = entity.Created,
                Modified = entity.Modified
            };
        }

        protected override Topic MapToEntity(TopicRequestTo request)
        {
            return new Topic
            {
                UserId = request.UserId,
                Title = request.Title,
                Content = request.Content
            };
        }
    }
}