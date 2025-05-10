using System.Linq.Expressions;
using WebApplication1.DTO;
using WebApplication1.Entity;
using WebApplication1.Repository;

namespace WebApplication1.Service
{
    public class TagService : ITagService
    {
        private readonly IRepository<Tag> _tagRepo;

        public TagService(IRepository<Tag> tagRepo)
        {
            _tagRepo = tagRepo;
        }
        public async Task<TagResponseTo> CreateTagAsync(TagRequestTo dto)
        {
            if (string.IsNullOrWhiteSpace(dto.Name))
            {
                throw new ValidationException("Tag name cannot be empty", 400, "40005");
            }
            if (dto.Name.Length < 3)
            {
                throw new ValidationException("Tag name must be at least 3 characters", 400, "40006");
            }
            if (dto.Name.Length > 20)
            {
                throw new ValidationException("Tag name is too long", 400, "40007");
            }

            var tag = new Tag
            {
                Name = dto.Name
            };

            var created = await _tagRepo.CreateAsync(tag);

            var digits = new string(dto.Name.Where(char.IsDigit).ToArray());
            if (!string.IsNullOrEmpty(digits))
            {
                string[] colors = { "red", "green", "blue" };
                foreach (var color in colors)
                {
                    var autoTag = new Tag
                    {
                        Name = $"{color}{digits}"
                    };
                    await _tagRepo.CreateAsync(autoTag);
                }
            }

            return new TagResponseTo
            {
                Id = created.Id,
                Name = created.Name
            };
        }


        public async Task DeleteTagAsync(long id)
        {
            var existing = await _tagRepo.GetByIdAsync(id);
            if (existing == null)
            {
                throw new ValidationException($"Tag with id {id} not found", 404, "40408");
            }
            await _tagRepo.DeleteAsync(id);
        }
        public async Task<PaginatedResult<TagResponseTo>> GetAllTagsAsync(
            int pageNumber = 1,
            int pageSize = 10)
        {
            Expression<Func<Tag, bool>> filter = s =>
                !s.Name.StartsWith("red") &&
                !s.Name.StartsWith("green") &&
                !s.Name.StartsWith("blue");

            var pagedTags = await _tagRepo.GetAllAsync(
                pageNumber,
                pageSize,
                filter,
                orderBy: q => q.OrderByDescending(s => s.Id)
            );

            var resultDto = new PaginatedResult<TagResponseTo>(
                pagedTags.Items.Select(s => new TagResponseTo
                {
                    Id = s.Id,
                    Name = s.Name
                }),
                pagedTags.TotalCount,
                pagedTags.PageNumber,
                pagedTags.PageSize);
            return resultDto;
        }


        public async Task<TagResponseTo> GetTagByIdAsync(long id)
        {
            var tag = await _tagRepo.GetByIdAsync(id);
            if (tag == null)
            {
                throw new ValidationException($"Tag with id {id} not found", 404, "40406");
            }
            return new TagResponseTo
            {
                Id = tag.Id,
                Name = tag.Name
            };
        }
        public async Task<TagResponseTo> UpdateTagAsync(long id, TagRequestTo dto)
        {
            var existing = await _tagRepo.GetByIdAsync(id);
            if (existing == null)
            {
                throw new ValidationException($"Tag with id {id} not found", 404, "40407");
            }
            if (string.IsNullOrWhiteSpace(dto.Name))
            {
                throw new ValidationException("Tag name cannot be empty", 400, "40005");
            }
            if (dto.Name.Length < 3)
            {
                throw new ValidationException("Tag name must be at least 3 characters", 400, "40006");
            }
            if (dto.Name.Length > 20)
            {
                throw new ValidationException("Tag name is too long", 400, "40007");
            }

            existing.Name = dto.Name;
            await _tagRepo.UpdateAsync(existing);
            var updatedEntity = await _tagRepo.GetByIdAsync(id);
            return new TagResponseTo
            {
                Id = updatedEntity.Id,
                Name = updatedEntity.Name
            };
        }

    }
}
