using AutoMapper;
using DistComp.Data;
using DistComp.Models;
using Microsoft.EntityFrameworkCore;

namespace DistComp.Services.Implementation {
    public class TopicService : ICommonService<long, Topic, TopicInDto, TopicOutDto> {

        private DCContext context;
        private IMapper mapper;

        public TopicService(DCContext context, IMapper mapper) {
            this.context = context;
            this.mapper = mapper;
        }

        public IEnumerable<TopicOutDto> GetAll() {
            return context.Topics.ToList().Select(e => mapper.Map<TopicOutDto>(e));
        }

        public TopicOutDto? Get(long id) {
            var topic = context.Topics.Find(id);
            if (topic == null) {
                return null;
            } else {
                return mapper.Map<TopicOutDto>(topic);
            }
        }


        public TopicOutDto Create(TopicInDto data) {
            var newTopic = mapper.Map<Topic>(data);
            context.Topics.Add(newTopic);

            foreach (var tagString in newTopic.TagStrings) {
                var tag = context.Tags.FirstOrDefault(t => t.Name == tagString);
                if (tag == null) {
                    newTopic.Tags.Add(new Tag { Name = tagString });
                } else {
                    tag.Topics.Add(newTopic);
                }
            }
            context.SaveChanges();

            return mapper.Map<TopicOutDto>(newTopic);
        }

        public TopicOutDto Update(Topic data) {
            var topic = context.Topics.Find(data.Id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            topic.UserId = data.UserId;
            topic.Title = data.Title;
            topic.Content = data.Content;
            context.SaveChanges();

            return mapper.Map<TopicOutDto>(data);
        }

        public void Delete(long id) {
            var topic = context.Topics
                .Include(topic => topic.Tags)
                .FirstOrDefault(t => t.Id == id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            context.Topics.Remove(topic);
            context.SaveChanges();

            foreach (var tag in topic.Tags) {
                if (!context.Tags.Where(t => t.Id == tag.Id).SelectMany(t => t.Topics).Any()) {
                    context.Tags.Remove(tag);
                }
            }
            context.SaveChanges();
        }
    }
}
