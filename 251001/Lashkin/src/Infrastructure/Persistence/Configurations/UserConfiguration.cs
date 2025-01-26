using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class UserConfiguration : IEntityTypeConfiguration<User>
{
    public void Configure(EntityTypeBuilder<User> builder)
    {
        builder.HasKey(user => user.Id);
        
        builder.HasIndex(user => user.Login).IsUnique();
        
        builder.Property(user => user.Login).HasMaxLength(64).IsRequired();
        
        builder.Property(user => user.Password).HasMaxLength(128).IsRequired();
        
        builder.Property(user => user.FirstName).HasMaxLength(64).IsRequired();
        
        builder.Property(user => user.LastName).HasMaxLength(64).IsRequired();
        
        builder.HasMany(user => user.News).WithOne(news => news.User).HasForeignKey(news => news.UserId).IsRequired().OnDelete(DeleteBehavior.Restrict);

        builder.HasData(new
        {
            Login = "lashkin2004@gmail.com",
            Password = "1234",
            FirstName = "Владислав",
            LastName = "Лашкин"
        });
    }
}