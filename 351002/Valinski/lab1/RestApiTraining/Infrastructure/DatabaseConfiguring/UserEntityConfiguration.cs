using Domain.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.DatabaseConfiguring;

public class UserEntityConfiguration : IEntityTypeConfiguration<User>
{
    public void Configure(EntityTypeBuilder<User> builder)
    {
        builder.ToTable("tbl_user");

        builder.HasKey(u => u.Id);
        builder.Property(u => u.Id)
            .ValueGeneratedOnAdd()
            .HasColumnName("id");
        
        builder.HasMany(x => x.Topics)
            .WithOne(x => x.User)
            .HasForeignKey(x => x.UserId)
            .OnDelete(DeleteBehavior.Cascade);
        
        builder.Property(u => u.Login)
            .IsRequired()
            .HasMaxLength(50)
            .HasColumnName("login");

        builder.HasIndex(u => u.Login)
            .IsUnique();

        builder.Property(u => u.Password)
            .HasMaxLength(128)
            .HasColumnName("password");
        
        builder.Property(u => u.Firstname)
            .IsRequired()
            .HasMaxLength(64)
            .HasColumnName("firstname");
        
        builder.Property(u => u.Lastname)
            .IsRequired()
            .HasMaxLength(64)
            .HasColumnName("lastname");
    }
}
