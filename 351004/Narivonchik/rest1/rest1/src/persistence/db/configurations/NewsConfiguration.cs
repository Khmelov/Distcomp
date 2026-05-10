using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using rest1.core.entities;

namespace rest1.persistence.db.configurations;

public class NewsConfiguration : IEntityTypeConfiguration<News>
{
    public void Configure(EntityTypeBuilder<News> builder)
    {
        builder.ToTable("tbl_news", schema: "public");
        builder.Property(n => n.Id).HasColumnName("id");
        builder.Property(n => n.CreatorId).HasColumnName("creator_id");
        builder.Property(n => n.Title).HasColumnName("title");
        builder.Property(n => n.Content).HasColumnName("content");
        builder.Property(n => n.CreatedAt).HasColumnName("created");
        builder.Property(n => n.Modified).HasColumnName("modified");
        builder.HasMany(n => n.Marks)
            .WithMany(m => m.News)
            .UsingEntity<Dictionary<string, object>>("tbl_newsMark",
                j => j.HasOne<Mark>()
                    .WithMany()
                    .HasForeignKey("MarkId")
                    .HasConstraintName("FK_MarkNews_Mark"),
                j => j.HasOne<News>()
                    .WithMany()
                    .HasForeignKey("NewsId")
                    .HasConstraintName("FK_MarkNews_News")
                    .OnDelete(DeleteBehavior.Cascade),
                j => j.HasKey("MarkId", "NewsId")
            );
    }
}
