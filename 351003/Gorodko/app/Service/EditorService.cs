using AutoMapper;
using Project.Dto;
using Project.Exceptions;
using Project.Model;
using Project.Repository;

namespace Project.Service {
    public class EditorService : BaseService<Editor, EditorRequestTo, EditorResponseTo> {
        public EditorService(IRepository<Editor> repository, IMapper mapper, ILogger<EditorService> logger)
            : base(repository, mapper, logger) {
        }

        public async Task<EditorResponseTo?> CreateEditorAsync(EditorRequestTo request) {
            if (string.IsNullOrWhiteSpace(request.Firstname) ||
                string.IsNullOrWhiteSpace(request.Lastname)) {
                throw new ValidationException("Firstname and lastname are required");
            }

            return await AddAsync(request);
        }
    }
}