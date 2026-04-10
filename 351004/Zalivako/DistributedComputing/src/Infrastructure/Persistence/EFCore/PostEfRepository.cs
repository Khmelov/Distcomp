using Application.Interfaces;
using Core.Entities;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Persistence.EFCore
{
    public class PostEfRepository : EfRepository<Post>, IPostRepository
    {
        public PostEfRepository(AppDbContext dbContext) : base(dbContext)
        {
        }
    }
}