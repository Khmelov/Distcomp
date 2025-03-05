using DTO.requests;
using DTO.responces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.abstractions
{
    public interface ITagService
    {
        Task<TagResponseTo> CreateAsync(TagRequestTo request);
        Task<TagResponseTo> GetByIdAsync(long id);
        Task<IEnumerable<TagResponseTo>> GetAllAsync();
        Task<TagResponseTo> UpdateAsync(long id, TagRequestTo request);
        Task<bool> DeleteAsync(long id);
    }
}
