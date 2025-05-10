using WebApplication1.DTO;
using WebApplication1.Entity;
using WebApplication1.Repository;

namespace WebApplication1.Service
{
    public class EditorService : IEditorService
    {
        private readonly IRepository<Editor> _editorRepo;

        public EditorService(IRepository<Editor> editorRepo)
        {
            _editorRepo = editorRepo;
        }

        public async Task<EditorResponseTo> CreateEditorAsync(EditorRequestTo dto)
        {
            if (string.IsNullOrWhiteSpace(dto.Login) || dto.Login.Length < 3 || dto.Login.Length > 64)
            {
                throw new ValidationException("Login must be between 3 and 64 characters", 400, "40001");
            }
            if (string.IsNullOrWhiteSpace(dto.Password) || dto.Password.Length < 8 || dto.Password.Length > 128)
            {
                throw new ValidationException("Password must be at least 8 characters", 400, "40004");
            }
            if (string.IsNullOrWhiteSpace(dto.Firstname) || dto.Firstname.Length < 3)
            {
                throw new ValidationException("Firstname must be at least 3 characters", 400, "40002");
            }
            if (string.IsNullOrWhiteSpace(dto.Lastname) || dto.Lastname.Length < 3)
            {
                throw new ValidationException("Lastname must be at least 3 characters", 400, "40003");
            }

            var editors = await _editorRepo.GetAllAsync(1, 1000);
            if (editors.Items.Any(u => u.Login.Equals(dto.Login, StringComparison.OrdinalIgnoreCase)))
            {
                throw new ValidationException($"Editor with login '{dto.Login}' already exists", 403, "40301");
            }

            var editor = new Editor
            {
                Login = dto.Login,
                Password = dto.Password,
                Firstname = dto.Firstname,
                Lastname = dto.Lastname
            };

            var created = await _editorRepo.CreateAsync(editor);
            return new EditorResponseTo
            {
                id = created.Id,
                login = created.Login,
                firstname = created.Firstname,
                lastname = created.Lastname
            };
        }

        public async Task<PaginatedResult<EditorResponseTo>> GetAllEditorsAsync(int pageNumber = 1, int pageSize = 10)
        {
            var pagedEditors = await _editorRepo.GetAllAsync(pageNumber, pageSize);
            var resultDto = new PaginatedResult<EditorResponseTo>(
                pagedEditors.Items.Select(u => new EditorResponseTo
                {
                    id = u.Id,
                    login = u.Login,
                    firstname = u.Firstname,
                    lastname = u.Lastname
                }),
                pagedEditors.TotalCount,
                pagedEditors.PageNumber,
                pagedEditors.PageSize);
            return resultDto;
        }

        public async Task<EditorResponseTo> GetEditorByIdAsync(long id)
        {
            var editor = await _editorRepo.GetByIdAsync(id);
            if (editor == null)
            {
                throw new ValidationException($"Editor with id {id} not found", 404, "40401");
            }
            return new EditorResponseTo
            {
                id = editor.Id,
                login = editor.Login,
                firstname = editor.Firstname,
                lastname = editor.Lastname
            };
        }

        public async Task<EditorResponseTo> UpdateEditorAsync(long id, EditorRequestTo dto)
        {
            var existing = await _editorRepo.GetByIdAsync(id);
            if (existing == null)
            {
                throw new ValidationException($"Editor with id {id} not found", 404, "40403");
            }
            if (string.IsNullOrWhiteSpace(dto.Login) || dto.Login.Length < 3 || dto.Login.Length > 64)
            {
                throw new ValidationException("Login must be between 3 and 64 characters", 400, "40011");
            }
            if (string.IsNullOrWhiteSpace(dto.Firstname) || dto.Firstname.Length < 3)
            {
                throw new ValidationException("Firstname must be at least 3 characters", 400, "40012");
            }
            if (string.IsNullOrWhiteSpace(dto.Lastname) || dto.Lastname.Length < 3)
            {
                throw new ValidationException("Lastname must be at least 3 characters", 400, "40013");
            }

            if (!existing.Login.Equals(dto.Login, StringComparison.OrdinalIgnoreCase))
            {
                var editors = await _editorRepo.GetAllAsync(1, 1000);
                if (editors.Items.Any(u => u.Login.Equals(dto.Login, StringComparison.OrdinalIgnoreCase)))
                {
                    throw new ValidationException($"Editor with login '{dto.Login}' already exists", 403, "40302");
                }
            }

            existing.Login = dto.Login;
            existing.Password = dto.Password;
            existing.Firstname = dto.Firstname;
            existing.Lastname = dto.Lastname;

            var updated = await _editorRepo.UpdateAsync(existing);
            return new EditorResponseTo
            {
                id = updated.Id,
                login = updated.Login,
                firstname = updated.Firstname,
                lastname = updated.Lastname
            };
        }

        public async Task DeleteEditorAsync(long id)
        {
            var existing = await _editorRepo.GetByIdAsync(id);
            if (existing == null)
            {
                throw new ValidationException($"Editor with id {id} not found", 404, "40408");
            }
            await _editorRepo.DeleteAsync(id);
        }
    }
}
