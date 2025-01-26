using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class NoticeConfiguration : IEntityTypeConfiguration<Notice>
{
    public void Configure(EntityTypeBuilder<Notice> builder)
    {
        builder.HasKey(notice => notice.Id);

        builder.Property(notice => notice.NewsId).IsRequired();
        
        builder.Property(notice => notice.Content).HasMaxLength(2048).IsRequired();
        
        builder.HasOne(notice => notice.News).WithMany(news => news.Notices).HasForeignKey(notice => notice.NewsId).IsRequired().OnDelete(DeleteBehavior.Restrict);
    }
}