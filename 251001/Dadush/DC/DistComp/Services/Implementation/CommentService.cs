using AutoMapper;
using DistComp.Data;
using DistComp.Models;
using Microsoft.EntityFrameworkCore;

namespace DistComp.Services.Implementation {
    public class CommentService : ICommonAsyncService<long, Comment, CommentInDto, CommentOutDto> {

        private DCContext context;
        private IMapper mapper;

        public CommentService(DCContext context, IMapper mapper) {
            this.context = context;
            this.mapper = mapper;
        }

        public async Task<IEnumerable<CommentOutDto>> GetAll() {
            return (await context.Comments.ToListAsync()).Select(e => mapper.Map<CommentOutDto>(e));
        }

        public async Task<CommentOutDto?> Get(long id) {
            var comment = await context.Comments.FindAsync(id);
            if (comment == null) {
                return null;
            } else {
                return mapper.Map<CommentOutDto>(comment);
            }
        }

        public async Task<CommentOutDto> Create(CommentInDto data) {
            var newComment = mapper.Map<Comment>(data);
            await context.Comments.AddAsync(newComment);
            await context.SaveChangesAsync();

            return mapper.Map<CommentOutDto>(newComment);
        }

        public async Task<CommentOutDto> Update(Comment data) {
            var comment = await context.Comments.FindAsync(data.Id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            comment.Content = data.Content;
            await context.SaveChangesAsync();

            return mapper.Map<CommentOutDto>(data);
        }

        public async Task Delete(long id) {
            var comment = await context.Comments.FirstOrDefaultAsync(t => t.Id == id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            context.Comments.Remove(comment);
            await context.SaveChangesAsync();
        }

    }
}
