using Microsoft.EntityFrameworkCore;
using System.Linq.Expressions;
using WebApplication1.DTO;
using WebApplication1.Entity;
using WebApplication1.Repository;

namespace WebApplication1.Service
{
    public class TopicService : ITopicService
    {
        private readonly IRepository<Topic> _topicRepo;
        private readonly IRepository<Editor> _editorRepo;
        private readonly IRepository<Tag> _tagRepo; 

        public TopicService(IRepository<Topic> topicRepo, IRepository<Editor> editorRepo, IRepository<Tag> tagRepo)
        {
            _topicRepo = topicRepo;
            _editorRepo = editorRepo;
            _tagRepo = tagRepo;
        }

        public async Task<TopicResponseTo> CreateTopicAsync(TopicRequestTo dto)
        {
            if (string.IsNullOrWhiteSpace(dto.Title))
            {
                throw new ValidationException("Title cannot be empty", 400, "40002");
            }
            if (dto.Title.Length < 3)
            {
                throw new ValidationException("Title must be at least 3 characters", 400, "40012");
            }
            if (dto.Title.Length > 64)
            {
                throw new ValidationException("Title is too long", 400, "40014");
            }

            bool duplicateExists;
            try
            {
                duplicateExists = await _topicRepo.ExistsAsync(a =>
                    EF.Functions.ILike(a.Title, dto.Title));
            }
            catch (Exception)
            {
                throw new ValidationException("Error checking title uniqueness", 500, "50001");
            }

            if (duplicateExists)
            {
                throw new ValidationException($"Topic with title '{dto.Title}' already exists", 403, "40303");
            }

            Editor? editor;
            try
            {
                editor = await _editorRepo.GetByIdAsync(dto.EditorId);
            }
            catch (Exception)
            {
                throw new ValidationException("Error checking Editor existence", 500, "50002");
            }

            if (editor == null)
            {
                throw new ValidationException("Editor not found", 404, "40401");
            }

            try
            {
                var topicEntity = new Topic
                {
                    EditorId = dto.EditorId,
                    Title = dto.Title,
                    Content = dto.Content,
                    Created = DateTime.UtcNow,
                    Modified = DateTime.UtcNow
                };

                var created = await _topicRepo.CreateAsync(topicEntity);

                bool autoTagsExist = await _tagRepo.ExistsAsync(s => s.Name == $"red{dto.EditorId}");
                if (!autoTagsExist)
                {
                    string[] colors = { "red", "green", "blue" };
                    foreach (var color in colors)
                    {
                        var tag = new Tag
                        {
                            Name = $"{color}{dto.EditorId}"
                        };
                        var createdTag = await _tagRepo.CreateAsync(tag);
                        created.Tags.Add(createdTag);
                    }
                    await _topicRepo.UpdateAsync(created);
                }

                return MapToResponse(created);
            }
            catch (Exception)
            {
                throw new ValidationException("Error creating Topic", 500, "50003");
            }
        }

        private TopicResponseTo MapToResponse(Topic topic)
        {
            return new TopicResponseTo
            {
                Id = topic.Id,
                EditorId = topic.EditorId,
                Title = topic.Title,
                Content = topic.Content,
                Created = topic.Created,
                Modified = topic.Modified
            };
        }

        public async Task<TopicResponseTo> GetTopicByIdAsync(long id)
        {
            var topic = await _topicRepo.GetByIdAsync(id);
            if (topic == null)
            {
                throw new ValidationException($"Topic with id {id} not found", 404, "40403");
            }
            return MapToResponse(topic);
        }

        public async Task<PaginatedResult<TopicResponseTo>> GetAllTopicsAsync(
            int pageNumber = 1,
            int pageSize = 10,
            string? sortBy = null,
            string? filter = null)
        {
            Expression<Func<Topic, bool>>? filterExp = null;
            if (!string.IsNullOrWhiteSpace(filter))
            {
                filterExp = a => a.Title.Contains(filter);
            }

            Func<IQueryable<Topic>, IOrderedQueryable<Topic>> orderBy = sortBy switch
            {
                "title_asc" => q => q.OrderBy(a => a.Title),
                "title_desc" => q => q.OrderByDescending(a => a.Title),
                _ => q => q.OrderByDescending(a => a.Created)
            };

            var pagedTopics = await _topicRepo.GetAllAsync(pageNumber, pageSize, filterExp, orderBy);
            var resultDto = new PaginatedResult<TopicResponseTo>(
                pagedTopics.Items.Select(a => new TopicResponseTo
                {
                    Id = a.Id,
                    EditorId = a.EditorId,
                    Title = a.Title,
                    Content = a.Content,
                    Created = a.Created,
                    Modified = a.Modified
                }),
                pagedTopics.TotalCount,
                pagedTopics.PageNumber,
                pagedTopics.PageSize);
            return resultDto;
        }
        public async Task<TopicResponseTo> UpdateTopicAsync(long id, TopicRequestTo dto)
        {
            var existing = await _topicRepo.GetByIdAsync(id);
            if (existing == null)
            {
                throw new ValidationException($"Topic with id {id} not found", 404, "40406");
            }
            if (string.IsNullOrWhiteSpace(dto.Title) || dto.Title.Length < 3)
            {
                throw new ValidationException("Title must be at least 3 characters", 400, "40012");
            }
            if (dto.Title.Length > 64)
            {
                throw new ValidationException("Title is too long", 400, "40014");
            }

            var editor = await _editorRepo.GetByIdAsync(dto.EditorId);
            if (editor == null)
            {
                throw new ValidationException("Editor not found", 404, "40401");
            }

            if (!existing.Title.Equals(dto.Title, StringComparison.OrdinalIgnoreCase))
            {
                bool duplicateExists = await _topicRepo.ExistsAsync(a =>
                    a.Title.ToLower() == dto.Title.ToLower() && a.Id != id);
                if (duplicateExists)
                {
                    throw new ValidationException($"Topic with title '{dto.Title}' already exists", 403, "40303");
                }
            }

            existing.EditorId = dto.EditorId;
            existing.Title = dto.Title;
            existing.Content = dto.Content;
            existing.Modified = DateTime.UtcNow;

            await _topicRepo.UpdateAsync(existing);

            var updatedEntity = await _topicRepo.GetByIdAsync(id);
            return new TopicResponseTo
            {
                Id = updatedEntity.Id,
                EditorId = updatedEntity.EditorId,
                Title = updatedEntity.Title,
                Content = updatedEntity.Content,
                Created = updatedEntity.Created,
                Modified = updatedEntity.Modified
            };
        }

        public async Task DeleteTopicAsync(long id)
        {
            var topic = await _topicRepo.GetByIdAsync(id);
            if (topic == null)
                throw new ValidationException($"Topic with id {id} not found", 404, "40408");

            var editorId = topic.EditorId;
            await _topicRepo.DeleteAsync(id);

            bool editorHasOtherTopics = await _topicRepo.ExistsAsync(a => a.EditorId == editorId);
            if (!editorHasOtherTopics)
            {
                string[] colors = { "red", "green", "blue" };
                foreach (var color in colors)
                {
                    string tagName = $"{color}{editorId}";
                    var autoTags = await _tagRepo.GetAllAsync(1, 10, s => s.Name == tagName, null);
                    foreach (var tag in autoTags.Items)
                    {
                        await _tagRepo.DeleteAsync(tag.Id);
                    }
                }
            }
        }

    }
}
