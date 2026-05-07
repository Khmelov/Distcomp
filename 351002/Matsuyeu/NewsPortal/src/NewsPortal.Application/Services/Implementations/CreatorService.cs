using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Entities;
using NewsPortal.Models.Exceptions;
using NewsPortal.Models.Repositories.Abstractions;
using NewsPortal.Services.Abstractions;

namespace NewsPortal.Services.Implementations
{
    public class CreatorService : ICreatorService
    {
        private readonly IRepository<Creator> _creatorRepository;

        public CreatorService(IRepository<Creator> creatorRepository)
        {
            _creatorRepository = creatorRepository;
        }

        public async Task<IEnumerable<CreatorResponseTo>> GetAllCreatorsAsync()
        {
            var creators = await _creatorRepository.GetAllAsync();
            return creators.Select(c => MapToResponse(c));
        }

        public async Task<CreatorResponseTo?> GetCreatorByIdAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var creator = await _creatorRepository.GetByIdAsync(id);
            if (creator == null)
                throw new NotFoundException($"Creator with ID {id} not found");

            return MapToResponse(creator);
        }

        public async Task<CreatorResponseTo> CreateCreatorAsync(CreatorRequestTo creatorRequest)
        {
            // Проверка уникальности логина
            var existingCreator = await _creatorRepository.FindSingleAsync(c => c.Login == creatorRequest.Login);
            if (existingCreator != null)
                throw new ConflictException($"Login '{creatorRequest.Login}' is already taken");

            var creator = MapToEntity(creatorRequest);
            var addedCreator = await _creatorRepository.AddAsync(creator);
            return MapToResponse(addedCreator);
        }

        public async Task<bool> UpdateCreatorAsync(CreatorRequestTo creatorRequest)
        {
            if (creatorRequest.Id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var existingCreator = await _creatorRepository.GetByIdAsync(creatorRequest.Id);
            if (existingCreator == null)
                throw new NotFoundException($"Creator with ID {creatorRequest.Id} not found");

            // Проверка уникальности логина (исключая текущего)
            var duplicateCreator = await _creatorRepository.FindSingleAsync(c =>
                c.Login == creatorRequest.Login && c.Id != creatorRequest.Id);
            if (duplicateCreator != null)
                throw new ConflictException($"Login '{creatorRequest.Login}' is already taken");

            var creator = MapToEntity(creatorRequest);
            await _creatorRepository.UpdateAsync(creator);
            return true;
        }

        public async Task<bool> DeleteCreatorAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            // Проверяем существование перед удалением
            var existingCreator = await _creatorRepository.GetByIdAsync(id);
            if (existingCreator == null)
                throw new NotFoundException($"Creator with ID {id} not found");

            await _creatorRepository.DeleteAsync(id);
            return true;
        }

        public async Task<PagedResult<CreatorResponseTo>> GetPagedCreatorsAsync(QueryParameters parameters)
        {
            var pagedResult = await _creatorRepository.GetPagedAsync(parameters);

            return new PagedResult<CreatorResponseTo>
            {
                Items = pagedResult.Items.Select(MapToResponse),
                TotalCount = pagedResult.TotalCount,
                PageNumber = pagedResult.PageNumber,
                PageSize = pagedResult.PageSize
            };
        }

        private CreatorResponseTo MapToResponse(Creator creator)
        {
            return new CreatorResponseTo
            {
                Id = creator.Id,
                Login = creator.Login,
                FirstName = creator.FirstName,
                LastName = creator.LastName
            };
        }

        private Creator MapToEntity(CreatorRequestTo dto)
        {
            return new Creator
            {
                Id = dto.Id,
                Login = dto.Login,
                Password = dto.Password,
                FirstName = dto.FirstName,
                LastName = dto.LastName
            };
        }
    }
}