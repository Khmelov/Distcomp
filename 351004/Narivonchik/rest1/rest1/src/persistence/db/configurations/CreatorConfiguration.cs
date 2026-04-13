using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using rest1.core.entities;

namespace rest1.persistence.db.configurations;

public class CreatorConfiguration : IEntityTypeConfiguration<Creator>
{
    public void Configure(EntityTypeBuilder<Creator> builder)
    {
        builder.ToTable("tbl_creator", schema: "public");
        builder.Property(e => e.Id).HasColumnName("id");
        builder.Property(e => e.Login).HasColumnName("login");
        builder.Property(e => e.Password).HasColumnName("password");
        builder.Property(e => e.Firstname).HasColumnName("firstname");
        builder.Property(e => e.Lastname).HasColumnName("lastname");
    }
}