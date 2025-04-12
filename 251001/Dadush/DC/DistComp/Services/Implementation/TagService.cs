using AutoMapper;
using DistComp.Data;
using DistComp.Models;
using Microsoft.EntityFrameworkCore;

namespace DistComp.Services.Implementation {
    public class TagService : ICommonService<long, Tag, TagInDto, TagOutDto> {
        private DCContext context;
        private IMapper mapper;

        public TagService(DCContext context, IMapper mapper) {
            this.context = context;
            this.mapper = mapper;
        }

        public IEnumerable<TagOutDto> GetAll() {
            return context.Tags.ToList().Select(e => mapper.Map<TagOutDto>(e));
        }

        public TagOutDto? Get(long id) {
            var tag = context.Tags.Find(id);
            if (tag == null) {
                return null;
            } else {
                return mapper.Map<TagOutDto>(tag);
            }
        }


        public TagOutDto Create(TagInDto data) {
            var newTag = mapper.Map<Tag>(data);
            context.Tags.Add(newTag);
            context.SaveChanges();

            return mapper.Map<TagOutDto>(newTag);
        }

        public TagOutDto Update(Tag data) {
            var tag = context.Tags.Find(data.Id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            tag.Name = data.Name;
            context.SaveChanges();

            return mapper.Map<TagOutDto>(data);
        }

        public void Delete(long id) {
            var tag = context.Tags.Include(t => t.Topics).FirstOrDefault(t => t.Id == id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            if (tag.Topics.Count() > 0) {
                throw new InvalidOperationException("Cannot delete tag using in one or more topics");
            }

            context.Tags.Remove(tag);
            context.SaveChanges();
        }
    }
}
