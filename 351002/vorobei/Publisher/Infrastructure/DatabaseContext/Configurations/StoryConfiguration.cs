using DataAccess.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.DatabaseContext.Configurations
{
    public class StoryConfiguration : IEntityTypeConfiguration<Story>
    {
        public void Configure(EntityTypeBuilder<Story> builder)
        {
            builder.ToTable("tbl_story").HasKey(p => p.Id);
            builder.HasOne(s => s.Creator)
               .WithMany(c => c.Stories)
               .HasForeignKey(s => s.CreatorId)
               .OnDelete(DeleteBehavior.Cascade);

            builder.HasMany(s => s.Marks)
                   .WithMany(m => m.Stories)
                   .UsingEntity<Dictionary<string, object>>(
                       "tbl_story_mark",
                       j => j.HasOne<Mark>().WithMany()
                             .HasForeignKey("mark_id")
                             .OnDelete(DeleteBehavior.Cascade),
                       j => j.HasOne<Story>().WithMany()
                             .HasForeignKey("story_id")
                             .OnDelete(DeleteBehavior.Cascade));

        }
    }
}