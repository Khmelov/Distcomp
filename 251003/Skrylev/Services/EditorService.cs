using System.Net;

public interface IEditorService
{
    Task<EditorResponseTo> CreateEditorAsync(EditorRequestTo editorDto);
    Task<EditorResponseTo> GetEditorByIdAsync(int id);
    Task<IEnumerable<EditorResponseTo>> GetAllEditorsAsync();
    Task<EditorResponseTo> UpdateEditorAsync(Editor editorDto);
    Task<bool> DeleteEditorAsync(int id);
}
public class EditorService : IEditorService
{
    private readonly IGenericRepository<Editor> _editorRepository;

    public EditorService(IGenericRepository<Editor> editorRepository, ILogger<EditorService> logger)
    {
        _editorRepository = editorRepository;
    }

    public async Task<EditorResponseTo> CreateEditorAsync(EditorRequestTo editorDto)
    {
        if (string.IsNullOrEmpty(editorDto.Login) || string.IsNullOrEmpty(editorDto.Password))
            throw new ArgumentException("Login and Password are required.");

        var loginExists = await ((IGenericRepository<Editor>)_editorRepository).ExistsByLoginAsync(editorDto.Login);
        if (loginExists)
        {
            throw new InvalidOperationException("Editor with this login already exists");
        }

        var editor = new Editor
        {
            login = editorDto.Login,
            password = editorDto.Password,
            firstname = editorDto.Firstname,
            lastname = editorDto.Lastname
        };

        var createdEditor = await _editorRepository.AddAsync(editor);
        return MapToResponse(createdEditor);
    }

    public async Task<EditorResponseTo> GetEditorByIdAsync(int id)
    {
        var editor = await _editorRepository.GetByIdAsync(id);
        if (editor == null)
        {
            throw new KeyNotFoundException("Editor not found.");
        }
        return MapToResponse(editor);
    }

    public async Task<IEnumerable<EditorResponseTo>> GetAllEditorsAsync()
    {
        var editors = await _editorRepository.GetAllAsync();
        return editors.Select(MapToResponse);
    }

    public async Task<EditorResponseTo> UpdateEditorAsync(Editor editorDto)
    {
        var editor = await _editorRepository.GetByIdAsync(editorDto.id);
        if (editor == null)
            throw new KeyNotFoundException("Editor not found.");

        editor.login = editorDto.login;
        editor.password = editorDto.password;
        editor.firstname = editorDto.firstname;
        editor.lastname = editorDto.lastname;

        var updatedEditor = await _editorRepository.UpdateAsync(editor);
        return MapToResponse(updatedEditor);
    }

    public async Task<bool> DeleteEditorAsync(int id)
    {
        var editor = await _editorRepository.GetByIdAsync(id);
        if (editor == null)
        {
            return false;
        }

        return await _editorRepository.DeleteAsync(id);
    }

    private EditorResponseTo MapToResponse(Editor editor)
    {
        return new EditorResponseTo
        {
            Id = editor.id,
            Login = editor.login,
            Password = editor.password,
            Firstname = editor.firstname,
            Lastname = editor.lastname
        };
    }
}



