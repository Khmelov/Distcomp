using Core.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace Infrastructure.Persistence.EFCore.Configurations
{
    public class EditorConfiguration : IEntityTypeConfiguration<Editor>
    {
        public void Configure(EntityTypeBuilder<Editor> builder)
        {
            builder.ToTable("tbl_editor", schema: "public");
            builder.Property(e => e.Id).HasColumnName("id");
            builder.Property(e => e.Login).HasColumnName("login");
            builder.Property(e => e.Password).HasColumnName("password");
            builder.Property(e => e.Firstname).HasColumnName("firstname");
            builder.Property(e => e.Lastname).HasColumnName("lastname");
        }
    }
}
