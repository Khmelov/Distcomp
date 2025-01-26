using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class LabelConfiguration : IEntityTypeConfiguration<Label>
{
    public void Configure(EntityTypeBuilder<Label> builder)
    {
        builder.HasKey(label => label.Id);

        builder.HasIndex(label => label.Name).IsUnique();
        
        builder.Property(label => label.Name).HasMaxLength(32).IsRequired();
        
        builder.HasMany(label => label.News).WithMany(news => news.Labels);
    }
}