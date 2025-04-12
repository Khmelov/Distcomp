using distcomp.DTOs;
using distcomp.Models;
using distcomp.Repositories;

namespace distcomp.Services
{
    public class TagService
    {

        private readonly TagRepository _tagRepository;

        public TagService(TagRepository tagRepository)
        {
            _tagRepository = tagRepository;
        }

       
        public TagResponseTo CreateSticker(TagRequestTo tagRequest)
        {
            var tag = new Tag
            {
                Name = tagRequest.Name
            };

            var savedTag = _tagRepository.Save(tag);
            return MapToTagResponse(savedTag);
        }
        public TagResponseTo GetTagById(long id)
        {
            var tag = _tagRepository.FindById(id);

            return tag != null ? MapToTagResponse(tag) : null;
        }

        
        public List<TagResponseTo> GetAllTags()
        {
            var tags = _tagRepository.FindAll();
            var tagResponses = new List<TagResponseTo>();
            foreach (var tag in tags)
            {
                tagResponses.Add(MapToTagResponse(tag));
            }
            return tagResponses;
        }

        
        public TagResponseTo UpdateTag(long id, TagRequestTo tagRequest)
        {
            var tag = _tagRepository.FindById(id);
            if (tag == null)
                return null;

            tag.Name = tagRequest.Name;

            var updatedTag = _tagRepository.Update(tag);
            return MapToTagResponse(updatedTag);
        }

        
        public bool DeleteTag(long id)
        {
            return _tagRepository.DeleteById(id);
        }

        // transforming sticker into DTO
        private TagResponseTo MapToTagResponse(Tag tag)
        {
            return new TagResponseTo
            {
                Id = tag.Id,
                Name = tag.Name
            };
        }
    }
}
