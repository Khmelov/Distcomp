using DTO.requests;
using DTO.responces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.abstractions
{
    public interface IStoryService
    {
        Task<StoryResponseTo> CreateAsync(StoryRequestTo request);
        Task<StoryResponseTo> GetByIdAsync(long id);
        Task<IEnumerable<StoryResponseTo>> GetAllAsync();
        Task<StoryResponseTo> UpdateAsync(long id, StoryRequestTo request);
        Task<bool> DeleteAsync(long id);
    }
}
