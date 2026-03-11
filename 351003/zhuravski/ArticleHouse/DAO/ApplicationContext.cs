using ArticleHouse.DAO.Models;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO;

public class ApplicationContext : DbContext
{
    public DbSet<ArticleModel> Articles {get; set;} = null!;
    public DbSet<CreatorModel> Creators {get; set;} = null!;
    public DbSet<MarkModel> Marks {get; set;} = null!;
    public DbSet<CommentModel> Comments {get; set;} = null!;
    public DbSet<ArticleMark> ArticleMarks { get; set; } = null!;
    public ApplicationContext(DbContextOptions<ApplicationContext> options) : base(options) {}

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<ArticleMark>(entity =>
        {
            entity.HasKey(am => new { am.ArticleId, am.MarkId });

            entity.HasOne(am => am.Article)
                .WithMany(a => a.ArticleMarks)
                .HasForeignKey(am => am.ArticleId);

            entity.HasOne(am => am.Mark)
                .WithMany(m => m.ArticleMarks)
                .HasForeignKey(am => am.MarkId);

            entity.ToTable("article_mark");
        });
    }
}