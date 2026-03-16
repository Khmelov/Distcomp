using Microsoft.EntityFrameworkCore;
using rest_api.Entities;

namespace rest_api.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<User> Users { get; set; }
        public DbSet<Topic> Topics { get; set; }
        public DbSet<Reaction> Reactions { get; set; }
        public DbSet<Tag> Tags { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Связь Topic -> User
            modelBuilder.Entity<Topic>()
                .HasOne(t => t.User)
                .WithMany(u => u.Topics)
                .HasForeignKey(t => t.UserId);

            // Связь Reaction -> Topic
            modelBuilder.Entity<Reaction>()
                .HasOne(r => r.Topic)
                .WithMany(t => t.Reactions)
                .HasForeignKey(r => r.TopicId);

            // Настройка связи многие-ко-многим через TopicTag
            modelBuilder.Entity<TopicTag>()
                .HasKey(tt => new { tt.TopicId, tt.TagId }); 

            modelBuilder.Entity<TopicTag>()
                .HasOne(tt => tt.Topic)
                .WithMany(t => t.TopicTags)
                .HasForeignKey(tt => tt.TopicId)
                .OnDelete(DeleteBehavior.Restrict); 

            modelBuilder.Entity<TopicTag>()
                .HasOne(tt => tt.Tag)
                .WithMany(t => t.TopicTags)
                .HasForeignKey(tt => tt.TagId)
                .OnDelete(DeleteBehavior.Restrict);


            base.OnModelCreating(modelBuilder);
        }
    }
}