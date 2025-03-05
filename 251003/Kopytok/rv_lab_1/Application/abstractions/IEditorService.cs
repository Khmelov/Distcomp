using Core;
using DTO.requests;
using DTO.responces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.abstractions
{
    public interface IEditorService
    {
        Task<EditorResponseTo> CreateAsync(EditorRequestTo request);
        Task<EditorResponseTo> GetByIdAsync(long id);
        Task<IEnumerable<EditorResponseTo>> GetAllAsync();
        Task<EditorResponseTo> UpdateAsync(long id, EditorRequestTo request);
        Task<bool> DeleteAsync(long id);
    }
}
