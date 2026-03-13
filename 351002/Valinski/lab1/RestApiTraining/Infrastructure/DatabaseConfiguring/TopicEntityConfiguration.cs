using Domain.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.DatabaseConfiguring;

public class TopicEntityConfiguration : IEntityTypeConfiguration<Topic>
{
    public void Configure(EntityTypeBuilder<Topic> builder)
    {
        builder.ToTable("tbl_topic");
        
        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id)
            .ValueGeneratedOnAdd()
            .HasColumnName("id");
        
        builder.Property(x => x.UserId)
            .HasColumnName("user_id");
        
        builder.HasOne(x => x.User)
            .WithMany(x => x.Topics)
            .HasForeignKey(x => x.UserId)
            .OnDelete(DeleteBehavior.Cascade);
        
        builder.Property(x => x.Title)
            .IsRequired()
            .HasMaxLength(64)
            .HasColumnName("title");

        builder.HasIndex(x => x.Title)
            .IsUnique();
        
        builder.Property(x => x.Content)
            .IsRequired()
            .HasMaxLength(2048)
            .HasColumnName("content");

        builder.Property(x => x.CreatedAt)
            .ValueGeneratedOnAdd()
            .IsRequired()
            .HasDefaultValueSql("CURRENT_TIMESTAMP")
            .HasColumnName("created_at");
        
        builder.Property(x => x.ModifiedAt)
            .ValueGeneratedOnAddOrUpdate()
            .IsRequired()
            .HasDefaultValueSql("CURRENT_TIMESTAMP")
            .HasColumnName("modified_at");

        builder.HasMany(x => x.Labels)
            .WithMany(x => x.Topics);

        builder.HasMany(x => x.Reactions)
            .WithOne(x => x.Topic)
            .HasForeignKey(x => x.TopicId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}
