using Domain.Entities;

namespace Domain.Repository;

public interface IEditorRepository
{
    Task<Editor?> AddEditor(Editor editor);
    Task<Editor?> GetEditor(long editorId);
    Task<Editor?> RemoveEditor(long editorId);
    Task<Editor?> UpdateEditor(Editor editor);

    Task<IEnumerable<Editor?>?> GetAllEditors();
}