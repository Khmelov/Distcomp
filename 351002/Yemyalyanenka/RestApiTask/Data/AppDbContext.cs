using Microsoft.EntityFrameworkCore;
using RestApiTask.Models.Entities;

namespace RestApiTask.Data;

public sealed class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<Writer> Writers => Set<Writer>();
    public DbSet<Article> Articles => Set<Article>();
    public DbSet<Marker> Markers => Set<Marker>();
    public DbSet<Message> Messages => Set<Message>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Writer>(b =>
        {
            b.ToTable("tbl_writer");
            b.HasKey(x => x.Id);
            b.Property(x => x.Id).HasColumnName("id");
            b.Property(x => x.Login).HasColumnName("login").HasMaxLength(64).IsRequired();
            b.Property(x => x.Password).HasColumnName("password").HasMaxLength(128).IsRequired();
            b.Property(x => x.Firstname).HasColumnName("firstname").HasMaxLength(64).IsRequired();
            b.Property(x => x.Lastname).HasColumnName("lastname").HasMaxLength(64).IsRequired();
            b.HasIndex(x => x.Login).IsUnique();
        });

        modelBuilder.Entity<Article>(b =>
        {
            b.ToTable("tbl_article");
            b.HasKey(x => x.Id);
            b.Property(x => x.Id).HasColumnName("id");
            b.Property(x => x.WriterId).HasColumnName("writer_id").IsRequired();
            b.Property(x => x.Title).HasColumnName("title").HasMaxLength(64).IsRequired();
            b.Property(x => x.Content).HasColumnName("content").HasMaxLength(2048).IsRequired();
            b.Property(x => x.Created).HasColumnName("created").IsRequired();
            b.Property(x => x.Modified).HasColumnName("modified").IsRequired();

            // MarkerIds is not persisted directly; many-to-many via join table
            b.Ignore(x => x.MarkerIds);
            b.HasIndex(x => x.Title).IsUnique();
        });

        modelBuilder.Entity<Marker>(b =>
        {
            b.ToTable("tbl_marker");
            b.HasKey(x => x.Id);
            b.Property(x => x.Id).HasColumnName("id");
            b.Property(x => x.Name).HasColumnName("name").HasMaxLength(32).IsRequired();
            b.HasIndex(x => x.Name).IsUnique();
        });

        modelBuilder.Entity<Message>(b =>
        {
            b.ToTable("tbl_message");
            b.HasKey(x => x.Id);
            b.Property(x => x.Id).HasColumnName("id");
            b.Property(x => x.ArticleId).HasColumnName("article_id").IsRequired();
            b.Property(x => x.Content).HasColumnName("content").HasMaxLength(2048).IsRequired();
        });
    }
}

