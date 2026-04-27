using DataAccess.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.DatabaseContext.Configurations
{
    public class PostConfiguration : IEntityTypeConfiguration<Post>
    {
        public void Configure(EntityTypeBuilder<Post> builder)
        {
            builder.ToTable("tbl_post").HasKey(p => p.Id);
            builder.HasOne(p => p.Story)
               .WithMany(s => s.Posts)
               .HasForeignKey(s => s.StoryId)
               .OnDelete(DeleteBehavior.Cascade);
        }
    }
}