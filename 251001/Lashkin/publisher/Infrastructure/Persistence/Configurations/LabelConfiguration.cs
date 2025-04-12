using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class LabelConfiguration : IEntityTypeConfiguration<Label>
{
    public void Configure(EntityTypeBuilder<Label> builder)
    {
        builder.ToTable("tbl_label");

        builder.HasKey(label => label.Id).HasName("PK_tbl_label");

        builder.Property(label => label.Name).HasMaxLength(32).IsRequired();
        
        builder.HasIndex(label => label.Name).IsUnique();

        builder.HasMany(label => label.News)
            .WithMany(news => news.Labels);
    }
}