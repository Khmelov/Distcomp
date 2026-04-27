using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using RW.Domain.Entities;

namespace RW.Infrastructure.Data.Configurations;

public class ArticleConfiguration : IEntityTypeConfiguration<Article>
{
    public void Configure(EntityTypeBuilder<Article> builder)
    {
        builder.ToTable("tbl_article");
        builder.HasKey(a => a.Id);
        builder.Property(a => a.Id).HasColumnName("id").UseIdentityAlwaysColumn();
        builder.Property(a => a.AuthorId).HasColumnName("author_id").IsRequired();
        builder.Property(a => a.Title).HasColumnName("Title").HasMaxLength(64).IsRequired();
        builder.HasIndex(a => a.Title).IsUnique();
        builder.Property(a => a.Content).HasColumnName("Content").HasMaxLength(2048).IsRequired();
        builder.Property(a => a.Created).HasColumnName("Created").IsRequired();
        builder.Property(a => a.Modified).HasColumnName("Modified").IsRequired();

        builder.HasOne<Author>()
            .WithMany()
            .HasForeignKey(a => a.AuthorId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}
