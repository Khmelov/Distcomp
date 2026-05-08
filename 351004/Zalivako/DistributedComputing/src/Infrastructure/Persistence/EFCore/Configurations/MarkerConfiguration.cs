using Core.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.Persistence.EFCore.Configurations
{
    public class MarkerConfiguration : IEntityTypeConfiguration<Marker>
    {
        public void Configure(EntityTypeBuilder<Marker> builder)
        {
            builder.ToTable("tbl_marker", schema: "public");
            builder.Property(m => m.Id).HasColumnName("id");
            builder.Property(m => m.Name).HasColumnName("name");
        }
    }
}