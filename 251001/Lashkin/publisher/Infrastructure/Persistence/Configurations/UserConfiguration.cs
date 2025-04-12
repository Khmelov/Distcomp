using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class UserConfiguration : IEntityTypeConfiguration<User>
{
    public void Configure(EntityTypeBuilder<User> builder)
    {
        builder.ToTable("tbl_user");
        
        builder.HasKey(user => user.Id).HasName("PK_tbl_user");

        builder.Property(user => user.Login).HasMaxLength(64).IsRequired();

        builder.HasIndex(user => user.Login).IsUnique();

        builder.Property(user => user.Password).HasMaxLength(128).IsRequired();

        builder.Property(user => user.FirstName).HasMaxLength(64).IsRequired();

        builder.Property(user => user.LastName).HasMaxLength(64).IsRequired();

        builder.HasMany(user => user.News)
            .WithOne(news => news.User)
            .OnDelete(DeleteBehavior.Cascade)
            .IsRequired();

        builder.HasData(new User
        {
            Id = (long)1,
            Login = "lashkin2004@gmail.com",
            Password = "1234",
            FirstName = "Владислав",
            LastName = "Лашкин"
        });
    }
}