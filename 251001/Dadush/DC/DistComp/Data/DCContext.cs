using DistComp.Models;
using Microsoft.EntityFrameworkCore;

namespace DistComp.Data {
    public class DCContext : DbContext {

        public DbSet<User> Users { get; set; }
        public DbSet<Topic> Topics { get; set; }
        public DbSet<Tag> Tags { get; set; }
        public DbSet<Comment> Comments { get; set; }

        public DCContext(DbContextOptions<DCContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder) {
            base.OnModelCreating(modelBuilder);
        }
    }
}
