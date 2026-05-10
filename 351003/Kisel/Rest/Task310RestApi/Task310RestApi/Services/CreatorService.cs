
using AutoMapper;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Exceptions;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;
using Task310RestApi.Repositories;

namespace Task310RestApi.Services
{
    public class CreatorService : ICreatorService
    {
        private readonly IRepository<Creator> _creatorRepository;
        private readonly InMemoryNewsRepository _newsRepository;
        private readonly IMapper _mapper;

        // Внутри CreatorService.cs
        public CreatorService(
            IRepository<Creator> creatorRepository,
            InMemoryNewsRepository newsRepository,
            IMapper mapper)
        {
            _creatorRepository = creatorRepository;
            _newsRepository = newsRepository;
            _mapper = mapper;
    
            // УДАЛИТЕ ИЛИ ЗАКОММЕНТИРУЙТЕ InitializeDefaultCreator() для тестов.
            // Если нужно оставить, уберите async void.
        }

        private async void InitializeDefaultCreator()
        {
            var existingCreators = await GetAllCreatorsAsync();
            if (!existingCreators.Any())
            {
                var defaultCreator = new CreatorRequestTo
                {
                    Login = "ipolina364@gmail.com",
                    Password = "defaultPassword123",
                    Firstname = "Irina",
                    Lastname = "Polina"
                };
                await CreateCreatorAsync(defaultCreator);
            }
        }

        public async Task<IEnumerable<CreatorResponseTo>> GetAllCreatorsAsync()
        {
            var creators = await _creatorRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<CreatorResponseTo>>(creators);
        }

        public async Task<CreatorResponseTo?> GetCreatorByIdAsync(long id)
        {
            var creator = await _creatorRepository.GetByIdAsync(id);
            if (creator == null)
            {
                throw new ResourceNotFoundException($"Creator not found with id: {id}");
            }
            return _mapper.Map<CreatorResponseTo>(creator);
        }

        public async Task<CreatorResponseTo> CreateCreatorAsync(CreatorRequestTo creatorRequest)
        {
            ValidateCreatorRequest(creatorRequest);
            
            var creator = _mapper.Map<Creator>(creatorRequest);
            creator.Created = DateTime.UtcNow;
            creator.Modified = DateTime.UtcNow;
            
            var createdCreator = await _creatorRepository.CreateAsync(creator);
            return _mapper.Map<CreatorResponseTo>(createdCreator);
        }

        public async Task<CreatorResponseTo?> UpdateCreatorAsync(long id, CreatorRequestTo creatorRequest)
        {
            ValidateCreatorRequest(creatorRequest);
            
            var existingCreator = await _creatorRepository.GetByIdAsync(id);
            if (existingCreator == null)
            {
                throw new ResourceNotFoundException($"Creator not found with id: {id}");
            }

            _mapper.Map(creatorRequest, existingCreator);
            existingCreator.Modified = DateTime.UtcNow;
            
            var updatedCreator = await _creatorRepository.UpdateAsync(existingCreator);
            return _mapper.Map<CreatorResponseTo>(updatedCreator);
        }

        public async Task<bool> DeleteCreatorAsync(long id)
        {
            if (!await _creatorRepository.ExistsAsync(id))
            {
                throw new ResourceNotFoundException($"Creator not found with id: {id}");
            }

            // Проверяем, есть ли у создателя новости
            var creatorNews = await _newsRepository.GetByCreatorIdAsync(id);
            if (creatorNews.Any())
            {
                throw new ValidationException("Cannot delete creator with existing news", "40001");
            }

            return await _creatorRepository.DeleteAsync(id);
        }

        public async Task<CreatorResponseTo?> GetCreatorByNewsIdAsync(long newsId)
        {
            var news = await _newsRepository.GetByIdAsync(newsId);
            if (news == null)
            {
                throw new ResourceNotFoundException($"News not found with id: {newsId}");
            }

            var creator = await _creatorRepository.GetByIdAsync(news.CreatorId);
            if (creator == null)
            {
                throw new ResourceNotFoundException($"Creator not found for news id: {newsId}");
            }

            return _mapper.Map<CreatorResponseTo>(creator);
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await _creatorRepository.ExistsAsync(id);
        }

        private void ValidateCreatorRequest(CreatorRequestTo request)
        {
            var validationResults = new List<System.ComponentModel.DataAnnotations.ValidationResult>();
            var validationContext = new System.ComponentModel.DataAnnotations.ValidationContext(request);
            
            if (!System.ComponentModel.DataAnnotations.Validator.TryValidateObject(request, validationContext, validationResults, true))
            {
                var errorMessages = string.Join("; ", validationResults.Select(r => r.ErrorMessage));
                throw new ValidationException(errorMessages, "40000");
            }
        }
    }
}
