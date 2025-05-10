using Microsoft.EntityFrameworkCore;
using WebApplication1.Entity;

namespace WebApplication1.Data
{
    public class AppDbContext : DbContext
    {
        public DbSet<Editor> Editors { get; set; } = null!;
        public DbSet<Topic> Topics { get; set; } = null!;
        public DbSet<Reaction> Reactions { get; set; } = null!;
        public DbSet<Tag> Tags { get; set; } = null!;

        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.HasDefaultSchema("distcomp");

            modelBuilder.Entity<Editor>(entity =>
            {
                entity.ToTable("tbl_editor");
                entity.HasKey(u => u.Id);
                entity.Property(u => u.Id).HasColumnName("id").ValueGeneratedOnAdd();
                entity.Property(u => u.Firstname).HasColumnName("firstname");
                entity.Property(u => u.Lastname).HasColumnName("lastname");
                entity.Property(u => u.Login).HasColumnName("login");
                entity.Property(u => u.Password).HasColumnName("password");
            });

            modelBuilder.Entity<Topic>(entity =>
            {
                entity.ToTable("tbl_topic");
                entity.HasKey(a => a.Id);
                entity.Property(a => a.Id).HasColumnName("id").ValueGeneratedOnAdd();
                entity.Property(a => a.EditorId).HasColumnName("editor_id");
                entity.Property(a => a.Title).HasColumnName("title");
                entity.Property(a => a.Content).HasColumnName("content");
                entity.Property(a => a.Created).HasColumnName("created");
                entity.Property(a => a.Modified).HasColumnName("modified");
                entity.HasOne(a => a.Editor)
                      .WithMany(u => u.Topics)
                      .HasForeignKey(a => a.EditorId);
            });

            modelBuilder.Entity<Reaction>(entity =>
            {
                entity.ToTable("tbl_reaction");
                entity.HasKey(n => n.Id);
                entity.Property(n => n.Id).HasColumnName("id").ValueGeneratedOnAdd();
                entity.Property(n => n.Content).HasColumnName("content");
                entity.HasOne(n => n.Topic)
                      .WithMany(a => a.Reactions)
                      .HasForeignKey(n => n.TopicId);
            });

            modelBuilder.Entity<Tag>(entity =>
            {
                entity.ToTable("tbl_tag");
                entity.HasKey(s => s.Id);
                entity.Property(s => s.Id).HasColumnName("id").ValueGeneratedOnAdd();
                entity.Property(s => s.Name).HasColumnName("name");
            });

            modelBuilder.Entity<Topic>()
                .HasMany(a => a.Tags)
                .WithMany(s => s.Topics)
                .UsingEntity<Dictionary<string, object>>(
                    "tbl_topic_tag",
                    j => {
                        return j.HasOne<Tag>()
                                .WithMany()
                                .HasForeignKey("tag_id")
                                .HasConstraintName("fk_topic_tag_tag");
                    },
                    j => {
                        return j.HasOne<Topic>()
                                .WithMany()
                                .HasForeignKey("topic_id")
                                .HasConstraintName("fk_topic_tag_topic");
                    },
                    j =>
                    {
                        j.HasKey("topic_id", "tag_id");
                        j.ToTable("tbl_topic_tag", schema: "distcomp");
                    }
                );
        }
    }
}
