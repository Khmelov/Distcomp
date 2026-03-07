using Application.Interfaces;
using Core.Entities;

namespace Infrastructure.Persistence.InMemory
{
    public class EditorInMemoryRepository : InMemoryRepository<Editor>, IEditorRepository
    {

    }
}
