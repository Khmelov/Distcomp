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

        // 1. Уникальный индекс для логина автора
        modelBuilder.Entity<Author>()
            .HasIndex(a => a.Login)
            .IsUnique();

        // 2. КАСКАДНОЕ УДАЛЕНИЕ: Author -> Articles
        modelBuilder.Entity<Article>()
            .HasOne(a => a.Author)
            .WithMany(a => a.Articles)
            .HasForeignKey(a => a.AuthorId)
            .OnDelete(DeleteBehavior.Cascade); // Если удален автор, удаляются его статьи

        // 3. КАСКАДНОЕ УДАЛЕНИЕ: Article -> Messages
        modelBuilder.Entity<Message>()
            .HasOne(m => m.Article)
            .WithMany(a => a.Messages)
            .HasForeignKey(m => m.ArticleId)
            .OnDelete(DeleteBehavior.Cascade); // Если удалена статья, удаляются ее сообщения

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