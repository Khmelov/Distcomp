using Domain.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.DatabaseConfiguring;

public class ReactionEntityConfiguration : IEntityTypeConfiguration<Reaction>
{
    public void Configure(EntityTypeBuilder<Reaction> builder)
    {
        builder.ToTable("tbl_reaction");
        
        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id)
            .ValueGeneratedOnAdd()
            .HasColumnName("id");

        builder.Property(x => x.Content)
            .IsRequired()
            .HasMaxLength(2048)
            .HasColumnName("content");
        
        builder.Property(x => x.TopicId)
            .IsRequired()
            .HasColumnName("topic_id");
        
        builder.HasOne(x => x.Topic)
            .WithMany(x => x.Reactions)
            .HasForeignKey(x => x.TopicId);
    }
}
