using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using RW.Domain.Entities;

namespace RW.Infrastructure.Data.Configurations;

public class TagConfiguration : IEntityTypeConfiguration<Tag>
{
    public void Configure(EntityTypeBuilder<Tag> builder)
    {
        builder.ToTable("tbl_tag");
        builder.HasKey(t => t.Id);
        builder.Property(t => t.Id).HasColumnName("id").UseIdentityAlwaysColumn();
        builder.Property(t => t.Name).HasColumnName("name").HasMaxLength(32).IsRequired();

        builder.HasMany(t => t.Articles)
            .WithMany(a => a.Tags)
            .UsingEntity(j => j.ToTable("tbl_article_tag"));
    }
}
