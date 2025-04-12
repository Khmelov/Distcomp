using AutoMapper;
using Discussion.Data;
using Discussion.Models;

namespace Discussion.Services.Implementation {
    public class CommentService : ICommentService {

        private readonly IMapper mapper;
        private ICommentRepository repository;

        public CommentService(IMapper mapper, ICommentRepository repository) {
            this.mapper = mapper;
            this.repository = repository;
        }

        public async Task<IEnumerable<CommentOutDto>> GetAllAsync() {
            return (await repository.GetAllAsync()).Select(e => mapper.Map<CommentOutDto>(e));
        }

        public async Task<CommentOutDto?> GetAsync(long id) {
            return mapper.Map<CommentOutDto>(await repository.GetAsync(id));
        }
        public async Task<CommentOutDto> CreateAsync(CommentInDto entity) {
            var comment = mapper.Map<Comment>(entity);
            var createdComment = await repository.CreateAsync(comment);
            return mapper.Map<CommentOutDto>(createdComment);
        }

        public async Task<CommentOutDto> UpdateAsync(Comment entity) {
            var comment = mapper.Map<Comment>(entity);
            var updatedComment = await repository.UpdateAsync(comment);
            return mapper.Map<CommentOutDto>(updatedComment);
        }

        public async Task DeleteAsync(long id) {
            await repository.DeleteAsync(id);
        }
    }
}
