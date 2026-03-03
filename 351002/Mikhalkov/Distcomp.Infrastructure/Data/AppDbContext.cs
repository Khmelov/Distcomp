using Distcomp.Domain.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace Distcomp.Infrastructure.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<User> Users { get; set; }
        public DbSet<Issue> Issues { get; set; }
        public DbSet<Marker> Markers { get; set; }
        public DbSet<Note> Notes { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<User>().ToTable("tbl_user");
            modelBuilder.Entity<Issue>().ToTable("tbl_issue");
            modelBuilder.Entity<Marker>().ToTable("tbl_marker");
            modelBuilder.Entity<Note>().ToTable("tbl_note");

            modelBuilder.Entity<Marker>(entity => {
                entity.ToTable("tbl_marker");
                entity.Property(m => m.Id).HasColumnName("id");
                entity.Property(m => m.Name).HasColumnName("name");
            });

            modelBuilder.Entity<Issue>()
                .HasMany(i => i.Markers)
                .WithMany(m => m.Issues)
                .UsingEntity<Dictionary<string, object>>(
                    "tbl_issue_marker",
                    j => j.HasOne<Marker>().WithMany().HasForeignKey("marker_id"),
                    j => j.HasOne<Issue>().WithMany().HasForeignKey("issue_id"),
                    j => { j.ToTable("tbl_issue_marker"); }
                );

            foreach (var entity in modelBuilder.Model.GetEntityTypes())
            {
                foreach (var property in entity.GetProperties())
                {
                    if (property.GetColumnName() == property.Name)
                    {
                        var name = property.Name.ToLower();
                        if (property.Name.EndsWith("Id") && property.Name.Length > 2)
                            name = property.Name.Replace("Id", "_id").ToLower();

                        property.SetColumnName(name);
                    }
                }
            }
        }
    }
}