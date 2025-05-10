using Distributed_Computing_Lab_2.Models;
using Microsoft.EntityFrameworkCore;

namespace Distributed_Computing_Lab_2.Data;

public class AppDbContext : DbContext
{
    public DbSet<Author> Authors { get; set; }
    public DbSet<Topic> Topics { get; set; }
    public DbSet<Message> Messages { get; set; }
    public DbSet<Sticker> Stickers { get; set; }
    
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) 
    { }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Для Author
        modelBuilder.Entity<Author>()
            .HasIndex(u => u.Login)
            .IsUnique();

        modelBuilder.Entity<Author>()
            .ToTable("tbl_author"); 
        
        // Для Sticker
        modelBuilder.Entity<Sticker>()
            .ToTable("tbl_sticker"); 
        
        // Для Topic
        modelBuilder.Entity<Topic>()
            .ToTable("tbl_topic"); 
        
        // Для Message
        modelBuilder.Entity<Message>()
            .ToTable("tbl_message"); 
        
        // Индексы для Topic и Sticker
        modelBuilder.Entity<Topic>()
            .HasIndex(s => s.Title)
            .IsUnique();

        modelBuilder.Entity<Sticker>()
            .HasIndex(t => t.Name)
            .IsUnique();
    }
}
