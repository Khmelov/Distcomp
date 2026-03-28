using Core.Entities;
using Infrastructure.Persistence.EFCore.Configurations;
using Microsoft.EntityFrameworkCore;


namespace Infrastructure.Persistence.EFCore
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options)
            : base(options) { }

        public DbSet<Editor> Editors { get; set; }

        // public DbSet<Post> Posts { get; set; }

        public DbSet<News> News { get; set; }

        public DbSet<Marker> Marker { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.ApplyConfiguration(new EditorConfiguration());
            modelBuilder.ApplyConfiguration(new MarkerConfiguration());
            //modelBuilder.ApplyConfiguration(new PostConfiguration());
            //modelBuilder.Ignore<Post>();
            modelBuilder.ApplyConfiguration(new NewsConfiguration());
            base.OnModelCreating(modelBuilder);
        }
    }
}
