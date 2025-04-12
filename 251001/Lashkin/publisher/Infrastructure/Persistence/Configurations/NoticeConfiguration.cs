using Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Persistence.Configurations;

public class NoticeConfiguration : IEntityTypeConfiguration<Notice>
{
    public void Configure(EntityTypeBuilder<Notice> builder)
    {
        builder.ToTable("tbl_notice");
        
        builder.HasKey(notice => notice.Id).HasName("PK_tbl_notice");

        builder.Property(notice => notice.NewsId).HasColumnName("notice_id");

        builder.Property(notice => notice.Content).HasMaxLength(2048).IsRequired();

        builder.HasOne(notice => notice.News)
            .WithMany(news => news.Notices)
            .OnDelete(DeleteBehavior.Cascade)
            .IsRequired();
    }
}