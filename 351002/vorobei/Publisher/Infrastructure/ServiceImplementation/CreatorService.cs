using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Repositories;
using DataAccess.Models;
using Infrastructure.Exceptions;

namespace Infrastructure.ServiceImplementation
{
    public class CreatorService : BaseService<Creator, CreatorRequestTo, CreatorResponseTo>
    {
        public CreatorService(IRepository<Creator> repository, IMapper mapper)
            : base(repository, mapper)
        { 
        }

        public async override Task<CreatorResponseTo> CreateAsync(CreatorRequestTo entity)
        {
            var allCreators = await _repository.GetAllAsync();
            var existingCreator = allCreators.FirstOrDefault(c => c.Login == entity.Login);
            if (existingCreator != null)
            {
                throw new BaseException(403, "Creator with such login already exists");
            }

            Creator creator = _mapper.Map<Creator>(entity);
            creator.Id = await _repository.GetLastIdAsync() + 1;
            
            await _repository.CreateAsync(creator);
            return _mapper.Map<CreatorResponseTo>(creator);
        }
    }
}
