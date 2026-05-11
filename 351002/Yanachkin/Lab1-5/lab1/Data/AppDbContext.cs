using lab1.Models.Entities;
using Microsoft.EntityFrameworkCore;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace lab1.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public DbSet<Editor> Editors => Set<Editor>();
    public DbSet<Issue> Issues => Set<Issue>();
    public DbSet<Label> Labels => Set<Label>();
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.HasDefaultSchema("distcomp");

        modelBuilder.Entity<Editor>(e =>
        {
            e.ToTable("tbl_editor");
            e.HasKey(x => x.Id);
            e.Property(x => x.Id).UseIdentityByDefaultColumn();
            e.Property(x => x.Login).HasMaxLength(64).IsRequired();
            e.HasIndex(x => x.Login).IsUnique();
            e.Property(x => x.Password).HasMaxLength(256).IsRequired();
            e.Property(x => x.FirstName).HasColumnName("firstname").HasMaxLength(128).IsRequired();
            e.Property(x => x.LastName).HasColumnName("lastname").HasMaxLength(128).IsRequired();
            e.HasMany(x => x.Issues)
                .WithOne(x => x.Editor)
                .HasForeignKey(x => x.EditorId)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Label>(e =>
        {
            e.ToTable("tbl_label");
            e.HasKey(x => x.Id);
            e.Property(x => x.Id).UseIdentityByDefaultColumn();
            e.Property(x => x.Name).HasMaxLength(128).IsRequired();
            e.HasIndex(x => x.Name).IsUnique();
        });

        modelBuilder.Entity<Issue>(e =>
        {
            e.ToTable("tbl_issue");
            e.HasKey(x => x.Id);
            e.Property(x => x.Id).UseIdentityByDefaultColumn();
            e.Property(x => x.Title).HasMaxLength(512).IsRequired();
            e.Property(x => x.Content).IsRequired();
            e.Property(x => x.Created).IsRequired();
            e.Property(x => x.Modified).IsRequired();
            e.HasMany(x => x.Labels)
                .WithMany(x => x.Issues)
                .UsingEntity<Dictionary<string, object>>(
                    "tbl_issue_label",
                    j => j.HasOne<Label>().WithMany().HasForeignKey("label_id").OnDelete(DeleteBehavior.Cascade),
                    j => j.HasOne<Issue>().WithMany().HasForeignKey("issue_id").OnDelete(DeleteBehavior.Cascade),
                    j =>
                    {
                        j.ToTable("tbl_issue_label");
                        j.HasKey("issue_id", "label_id");
                    });
        });

    }
}
