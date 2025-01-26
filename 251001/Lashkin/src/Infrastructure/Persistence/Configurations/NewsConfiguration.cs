using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class NewsConfiguration : IEntityTypeConfiguration<News>
{
    public void Configure(EntityTypeBuilder<News> builder)
    {
        builder.HasKey(news => news.Id);
        
        builder.Property(news => news.UserId).IsRequired();
        
        builder.HasIndex(news => news.Title).IsUnique();
        
        builder.Property(news => news.Title).HasMaxLength(64).IsRequired();
        
        builder.Property(news => news.Content).HasMaxLength(2048).IsRequired();
        
        builder.HasOne(news => news.User).WithMany(user => user.News).HasForeignKey(news => news.UserId).IsRequired().OnDelete(DeleteBehavior.Restrict);
        
        builder.HasMany(news => news.Notices).WithOne(notice => notice.News).HasForeignKey(notice => notice.NewsId).IsRequired().OnDelete(DeleteBehavior.Cascade);

        builder.HasMany(news => news.Labels).WithMany(label => label.News);
    }
}