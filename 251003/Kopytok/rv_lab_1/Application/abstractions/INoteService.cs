using DTO.requests;
using DTO.responces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.abstractions
{
    public interface INoteService
    {
        Task<NoteResponseTo> CreateAsync(NoteRequestTo request);
        Task<NoteResponseTo> GetByIdAsync(long id);
        Task<IEnumerable<NoteResponseTo>> GetAllAsync();
        Task<NoteResponseTo> UpdateAsync(long id, NoteRequestTo request);
        Task<bool> DeleteAsync(long id);
    }
}
