using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Publisher.Domain.Models;

namespace Publisher.Infrastructure.DbConfiguring;

public class LabelConfiguration : IEntityTypeConfiguration<Label>
{
    public void Configure(EntityTypeBuilder<Label> builder)
    {
        builder.ToTable("tbl_label");

        builder.HasKey(l => l.Id);
        builder.Property(l => l.Id).HasColumnName("id").ValueGeneratedOnAdd();

        builder.Property(l => l.Name)
            .HasColumnName("name")
            .IsRequired()
            .HasMaxLength(32);

        builder.HasMany(l => l.Topics)
            .WithMany(t => t.Labels)
            .UsingEntity(j => j.ToTable("tbl_m2m_topic_labels"));
    }
}
