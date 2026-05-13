using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Publisher.Domain.Models;

namespace Publisher.Infrastructure.DbConfiguring;

public class TopicConfiguration : IEntityTypeConfiguration<Topic>
{
    public void Configure(EntityTypeBuilder<Topic> builder)
    {
        builder.ToTable("tbl_topic");

        builder.HasKey(t => t.Id);
        builder.Property(t => t.Id)
            .HasColumnName("id")
            .ValueGeneratedOnAdd();

        builder.Property(t => t.Title)
            .HasColumnName("title")
            .IsRequired()
            .HasMaxLength(64);

        builder.Property(t => t.Content)
            .HasColumnName("content")
            .IsRequired()
            .HasMaxLength(2048);

        builder.Property(t => t.Created)
            .HasColumnName("created")
            .IsRequired();

        builder.Property(t => t.Modified)
            .HasColumnName("modified")
            .IsRequired();

        builder.HasOne(t => t.User)
            .WithMany(u => u.Topics)
            .HasForeignKey(t => t.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        builder.Property(t => t.UserId)
            .HasColumnName("user_id");
        
        builder.HasMany(t => t.Labels)
            .WithMany(l => l.Topics)
            .UsingEntity(j => j.ToTable("tbl_m2m_topic_labels"));
    }
}
