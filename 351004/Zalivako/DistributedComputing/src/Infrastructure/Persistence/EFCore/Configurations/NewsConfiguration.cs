using Microsoft.EntityFrameworkCore;
using Core.Entities;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.Persistence.EFCore.Configurations
{
    public class NewsConfiguration : IEntityTypeConfiguration<News>
    {
        public void Configure(EntityTypeBuilder<News> builder)
        {
            builder.ToTable("tbl_news", schema: "public");
            builder.Property(n => n.Id).HasColumnName("id");
            builder.Property(n => n.EditorId).HasColumnName("editor_id");
            builder.Property(n => n.Title).HasColumnName("title");
            builder.Property(n => n.Content).HasColumnName("content");
            builder.Property(n => n.CreatedAt).HasColumnName("created");
            builder.Property(n => n.Modified).HasColumnName("modified");
            builder.HasMany(n => n.Markers)
                .WithMany(m => m.News)
                .UsingEntity<Dictionary<string, object>>("MarkerNews",
                j => j.HasOne<Marker>()
                    .WithMany()
                    .HasForeignKey("MarkerId")
                    .HasConstraintName("FK_MarkerNews_Marker"),
                j => j.HasOne<News>()
                    .WithMany()
                    .HasForeignKey("NewsId")
                    .HasConstraintName("FK_MarkerNews_News")
                    .OnDelete(DeleteBehavior.Cascade),
                j => j.HasKey("MarkerId", "NewsId")
            );

        }
    }
}
