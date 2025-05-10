using Microsoft.EntityFrameworkCore;
using Publisher.Models;

namespace Publisher.Data;

public class AppDbContext : DbContext
{
    public DbSet<Author> Users { get; set; }
    public DbSet<Topic> Stories { get; set; }
    public DbSet<Sticker> Tags { get; set; }
    
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) 
    { }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Author>()
            .HasIndex(u => u.Login)
            .IsUnique();

        modelBuilder.Entity<Author>()
            .ToTable("tbl_author");
        
        modelBuilder.Entity<Sticker>()
            .ToTable("tbl_sticker");
        
        modelBuilder.Entity<Topic>()
            .ToTable("tbl_topic");
        
        modelBuilder.Entity<Topic>()
            .HasIndex(s => s.Title)
            .IsUnique();

        modelBuilder.Entity<Sticker>()
            .HasIndex(t => t.name)
            .IsUnique();
    }
}