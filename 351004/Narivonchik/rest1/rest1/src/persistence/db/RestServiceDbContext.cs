
using Microsoft.EntityFrameworkCore;
using rest1.core.entities;
using rest1.persistence.db.configurations;

namespace rest1.persistence.db;

public class RestServiceDbContext : DbContext
{
    public RestServiceDbContext(DbContextOptions<RestServiceDbContext> options)
        : base(options) { }

    public DbSet<Creator> Creators { get; set; }

    // public DbSet<Note> Notes { get; set; }

    public DbSet<News> News { get; set; }

    public DbSet<Mark> Marks { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfiguration(new CreatorConfiguration());
        modelBuilder.ApplyConfiguration(new MarkConfiguration());
        // modelBuilder.ApplyConfiguration(new NoteConfiguration());
        modelBuilder.ApplyConfiguration(new NewsConfiguration());
        base.OnModelCreating(modelBuilder);
    }
}
