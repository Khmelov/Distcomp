using AutoMapper;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Repositories;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace Publisher.Services
{
    public class TagService : BaseService<Tag, TagRequestTo, TagResponseTo>
    {
        public TagService(IRepository<Tag> repository, IMapper mapper)
            : base(repository, mapper)
        {
        }

        public override async Task<TagResponseTo> CreateAsync(TagRequestTo request)
        {
            // Проверка уникальности имени
            var existing = (await _repository.FindAsync(t => t.Name == request.Name)).FirstOrDefault();
            if (existing != null)
                throw new InvalidOperationException("Tag with this name already exists");

            var tag = _mapper.Map<Tag>(request);
            await _repository.AddAsync(tag);
            await _repository.SaveChangesAsync();

            return _mapper.Map<TagResponseTo>(tag);
        }

        public override async Task<TagResponseTo> UpdateAsync(long id, TagRequestTo request)
        {
            var tag = await _repository.GetByIdAsync(id);
            if (tag == null)
                throw new KeyNotFoundException($"Tag with id {id} not found");

            // Если имя меняется, проверяем уникальность
            if (tag.Name != request.Name)
            {
                var existing = (await _repository.FindAsync(t => t.Name == request.Name)).FirstOrDefault();
                if (existing != null)
                    throw new InvalidOperationException("Tag with this name already exists");
            }

            // AutoMapper обновляет существующую сущность
            _mapper.Map(request, tag);

            _repository.Update(tag);
            await _repository.SaveChangesAsync();

            return _mapper.Map<TagResponseTo>(tag);
        }
    }
}