using Domain.Models;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure;

public class BlogDbContext : DbContext
{
    public BlogDbContext(DbContextOptions options) : base(options)
    {
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(BlogDbContext).Assembly);
    }

    public DbSet<User> Users { get; set; }
    public DbSet<Topic> Topics { get; set; }
    public DbSet<Reaction> Reactions { get; set; }
    public DbSet<Label> Labels { get; set; }
}
