using rest_api.Entities;
using rest_api.InMemory;
using rest_api.Dtos;

namespace rest_api.Services
{
    public class TagService : BaseService<Tag, TagRequestTo, TagResponseTo>
    {
        public TagService(IRepository<Tag> repository) : base(repository)
        {
        }

        public override TagResponseTo Create(TagRequestTo request)
        {
            // Проверка уникальности имени (опционально)
            var existing = _repository.Find(t => t.Name == request.Name).FirstOrDefault();
            if (existing != null)
                throw new InvalidOperationException("Tag with this name already exists");

            var tag = MapToEntity(request);
            _repository.Add(tag);
            return MapToResponse(tag);
        }

        public override TagResponseTo Update(TagRequestTo request)
        {
            var id = request.Id;
            var tag = _repository.GetById(id);
            if (tag == null)
                throw new KeyNotFoundException($"Tag with id {id} not found");

            // Если имя меняется, проверяем уникальность
            if (tag.Name != request.Name)
            {
                var existing = _repository.Find(t => t.Name == request.Name).FirstOrDefault();
                if (existing != null)
                    throw new InvalidOperationException("Tag with this name already exists");
            }

            tag.Name = request.Name;
            _repository.Update(tag);
            return MapToResponse(tag);
        }

        protected override TagResponseTo MapToResponse(Tag entity)
        {
            return new TagResponseTo
            {
                Id = entity.Id,
                Name = entity.Name
            };
        }

        protected override Tag MapToEntity(TagRequestTo request)
        {
            return new Tag
            {
                Name = request.Name
            };
        }
    }
}