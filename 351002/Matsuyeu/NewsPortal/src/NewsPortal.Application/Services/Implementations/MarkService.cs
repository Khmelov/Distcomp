using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Entities;
using NewsPortal.Models.Exceptions;
using NewsPortal.Models.Repositories.Abstractions;
using NewsPortal.Services.Abstractions;

namespace NewsPortal.Services.Implementations
{
    public class MarkService : IMarkService
    {
        private readonly IRepository<Mark> _markRepository;

        public MarkService(IRepository<Mark> markRepository)
        {
            _markRepository = markRepository;
        }

        public async Task<IEnumerable<MarkResponseTo>> GetAllMarksAsync()
        {
            var marks = await _markRepository.GetAllAsync();
            return marks.Select(m => MapToResponse(m));
        }

        public async Task<MarkResponseTo?> GetMarkByIdAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var mark = await _markRepository.GetByIdAsync(id);
            if (mark == null)
                throw new NotFoundException($"Mark with ID {id} not found");

            return MapToResponse(mark);
        }

        public async Task<MarkResponseTo> CreateMarkAsync(MarkRequestTo markRequest)
        {
            // Проверка уникальности имени
            var existingMark = await _markRepository.FindSingleAsync(m => m.Name == markRequest.Name);
            if (existingMark != null)
                throw new ConflictException($"Mark name '{markRequest.Name}' is already taken");

            var mark = MapToEntity(markRequest);
            var addedMark = await _markRepository.AddAsync(mark);
            return MapToResponse(addedMark);
        }

        public async Task<bool> UpdateMarkAsync(MarkRequestTo markRequest)
        {
            if (markRequest.Id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            var existingMark = await _markRepository.GetByIdAsync(markRequest.Id);
            if (existingMark == null)
                throw new NotFoundException($"Mark with ID {markRequest.Id} not found");

            // Проверка уникальности имени (исключая текущую метку)
            var duplicateMark = await _markRepository.FindSingleAsync(m =>
                m.Name == markRequest.Name && m.Id != markRequest.Id);
            if (duplicateMark != null)
                throw new ConflictException($"Mark name '{markRequest.Name}' is already taken");

            var mark = MapToEntity(markRequest);
            await _markRepository.UpdateAsync(mark);
            return true;
        }

        public async Task<bool> DeleteMarkAsync(long id)
        {
            if (id <= 0)
                throw new BadRequestException("ID must be greater than 0");

            // Проверка, используется ли метка в новостях
            var mark = await _markRepository.GetByIdAsync(id);
            if (mark == null)
                throw new NotFoundException($"Mark with ID {id} not found");

            await _markRepository.DeleteAsync(id);
            return true;
        }

        public async Task<PagedResult<MarkResponseTo>> GetPagedMarksAsync(QueryParameters parameters)
        {
            var pagedResult = await _markRepository.GetPagedAsync(parameters);

            return new PagedResult<MarkResponseTo>
            {
                Items = pagedResult.Items.Select(MapToResponse),
                TotalCount = pagedResult.TotalCount,
                PageNumber = pagedResult.PageNumber,
                PageSize = pagedResult.PageSize
            };
        }

        #region Private Methods

        private MarkResponseTo MapToResponse(Mark mark)
        {
            return new MarkResponseTo
            {
                Id = mark.Id,
                Name = mark.Name
            };
        }

        private Mark MapToEntity(MarkRequestTo dto)
        {
            return new Mark
            {
                Id = dto.Id,
                Name = dto.Name.Trim()
            };
        }

        #endregion
    }
}