using Core.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.Persistence.EFCore.Configurations
{
    public class PostConfiguration : IEntityTypeConfiguration<Post>
    {
        public void Configure(EntityTypeBuilder<Post> builder)
        {
            builder.ToTable("tbl_post", schema: "public");
            builder.Property(p => p.Id).HasColumnName("id");
            builder.Property(p => p.NewsId).HasColumnName("news_id");
            builder.Property(p => p.Content).HasColumnName("content");
        }
    }
}