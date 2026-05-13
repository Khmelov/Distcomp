using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Publisher.Domain.Models;

namespace Publisher.Infrastructure.DbConfiguring;

public class UserConfiguration : IEntityTypeConfiguration<User>
{
    public void Configure(EntityTypeBuilder<User> builder)
    {
        builder.ToTable("tbl_user");

        builder.HasKey(u => u.Id);
        builder.Property(u => u.Id)
            .HasColumnName("id")
            .ValueGeneratedOnAdd();

        builder.Property(u => u.Login)
            .IsRequired()
            .HasColumnName("login")
            .HasMaxLength(64); 

        builder.Property(u => u.Password)
            .IsRequired()
            .HasColumnName("password")
            .HasMaxLength(128); 

        builder.Property(u => u.Firstname)
            .HasColumnName("firstname")
            .HasMaxLength(64); 

        builder.Property(u => u.Lastname)
            .HasColumnName("lastname")
            .HasMaxLength(64); 

        builder.HasData(new User
        {
            Id = 1,
            Login = "valinskiyartem@gmail.com",
            Password = "some_password", 
            Firstname = "Артём",
            Lastname = "Валинский"
        });
    }

}
