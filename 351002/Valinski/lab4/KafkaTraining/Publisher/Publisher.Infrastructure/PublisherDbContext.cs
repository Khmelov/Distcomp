using Microsoft.EntityFrameworkCore;
using Publisher.Domain.Models;

namespace Infrastructure
{
    public class PublisherDbContext : DbContext
    {
        public PublisherDbContext(DbContextOptions<PublisherDbContext> options) : base(options) { }

        public DbSet<User> Users => Set<User>();
        public DbSet<Topic> Topics => Set<Topic>();
        public DbSet<Label> Labels => Set<Label>();
        public DbSet<TopicLabel> TopicLabels => Set<TopicLabel>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("tbl_user");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Login).HasMaxLength(64).IsRequired();
                entity.Property(e => e.Password).HasMaxLength(128).IsRequired();
                entity.Property(e => e.Firstname).HasMaxLength(64).IsRequired();
                entity.Property(e => e.Lastname).HasMaxLength(64).IsRequired();
            });

            modelBuilder.Entity<Topic>(entity =>
            {
                entity.ToTable("tbl_topic");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Title).HasMaxLength(64).IsRequired();
                entity.Property(e => e.Content).HasMaxLength(2048).IsRequired();
                entity.Property(e => e.Created).HasDefaultValueSql("CURRENT_TIMESTAMP");
            
                entity.HasOne(d => d.User)
                    .WithMany(p => p.Topics)
                    .HasForeignKey(d => d.UserId)
                    .OnDelete(DeleteBehavior.Cascade);
            });

            modelBuilder.Entity<Label>(entity =>
            {
                entity.ToTable("tbl_label");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Name).HasMaxLength(32).IsRequired();
            });

            modelBuilder.Entity<TopicLabel>(entity =>
            {
                entity.ToTable("tbl_topic_label");
                entity.HasKey(e => e.Id);

                entity.HasOne(d => d.Topic)
                    .WithMany(p => p.TopicLabels)
                    .HasForeignKey(d => d.TopicId);

                entity.HasOne(d => d.Label)
                    .WithMany(p => p.TopicLabels)
                    .HasForeignKey(d => d.LabelId);
            });
        }
    }
}
