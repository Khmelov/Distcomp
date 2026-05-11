using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using RW.Domain.Entities;

namespace RW.Infrastructure.Data.Configurations;

public class AuthorConfiguration : IEntityTypeConfiguration<Author>
{
    public void Configure(EntityTypeBuilder<Author> builder)
    {
        builder.ToTable("tbl_author");
        builder.HasKey(a => a.Id);
        builder.Property(a => a.Id).HasColumnName("id").UseIdentityAlwaysColumn();
        builder.Property(a => a.Login).HasColumnName("Login").HasMaxLength(64).IsRequired();
        builder.HasIndex(a => a.Login).IsUnique();
        builder.Property(a => a.Password).HasColumnName("Password").HasMaxLength(128).IsRequired();
        builder.Property(a => a.FirstName).HasColumnName("FirstName").HasMaxLength(64).IsRequired();
        builder.Property(a => a.LastName).HasColumnName("LastName").HasMaxLength(64).IsRequired();
        builder.Property(a => a.Role)
            .HasColumnName("Role")
            .HasConversion<string>()
            .HasMaxLength(16)
            .IsRequired();
    }
}
