using Microsoft.EntityFrameworkCore;
using ServerApp.Models.Entities;

namespace ServerApp.Infrastructure;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<Author> Authors { get; set; }
    public DbSet<Article> Articles { get; set; }
    public DbSet<Message> Messages { get; set; }
    public DbSet<Sticker> Stickers { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Article>()
            .HasMany(s => s.Stickers)
            .WithMany(a => a.Articles)
            .UsingEntity<Dictionary<string, object>>(
                "tbl_article_sticker",
                j => j.HasOne<Sticker>().WithMany().HasForeignKey("sticker_id"),
                j => j.HasOne<Article>().WithMany().HasForeignKey("article_id")
            );
    }
}