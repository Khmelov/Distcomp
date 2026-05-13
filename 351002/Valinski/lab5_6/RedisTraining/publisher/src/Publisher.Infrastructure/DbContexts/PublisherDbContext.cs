using Microsoft.EntityFrameworkCore;
using Publisher.Domain.Models;

namespace Publisher.Infrastructure.DbContexts;

public class PublisherDbContext : DbContext
{
    public PublisherDbContext(DbContextOptions<PublisherDbContext> options) : base(options)
    { }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(PublisherDbContext).Assembly);
    }
    
    public DbSet<User> Users => Set<User>();
    public DbSet<Topic> Topics => Set<Topic>();
    public DbSet<Label> Labels => Set<Label>();
}
