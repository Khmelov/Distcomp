using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using rest1.core.entities;

namespace rest1.persistence.db.configurations;

public class MarkConfiguration : IEntityTypeConfiguration<Mark>
{
    public void Configure(EntityTypeBuilder<Mark> builder)
    {
        builder.ToTable("tbl_mark", schema: "public");
        builder.Property(m => m.Id).HasColumnName("id");
        builder.Property(m => m.Name).HasColumnName("name");
    }
}
