using Microsoft.EntityFrameworkCore;
using Redis.Models;

namespace Redis.Data;

public class PublisherDbContext : DbContext
{
    public PublisherDbContext(DbContextOptions<PublisherDbContext> options) : base(options) { }

    public DbSet<Creator> Creators { get; set; }
    public DbSet<News> News { get; set; }
    public DbSet<Label> Labels { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Настройка связи многие-ко-многим с префиксом tbl_
        modelBuilder.Entity<News>()
            .HasMany(n => n.Labels)
            .WithMany(l => l.News)
            .UsingEntity(j => j.ToTable("tbl_news_labels"));
    }
}