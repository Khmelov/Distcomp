using Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace Persistence;

public class RepositoryContext : DbContext
{
    public RepositoryContext(DbContextOptions<RepositoryContext> options) : base(options)
    {
    }
    
    public DbSet<User> Users { get; set; }
    public DbSet<News> News { get; set; }
    public DbSet<Notice> Notices { get; set; }
    public DbSet<Label> Labels { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(RepositoryContext).Assembly);
        
        base.OnModelCreating(modelBuilder);
    }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseInMemoryDatabase(databaseName: "rest");
        
        base.OnConfiguring(optionsBuilder);
    }
}