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
            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("tbl_user");
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Login).HasColumnName("login");
                entity.Property(e => e.Password).HasColumnName("password");
                entity.Property(e => e.Firstname).HasColumnName("firstname");
                entity.Property(e => e.Lastname).HasColumnName("lastname");
            });
            // Тема
            modelBuilder.Entity<Topic>(entity =>
            {
                entity.ToTable("tbl_topic");
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.UserId).HasColumnName("user_id");
                entity.Property(e => e.Title).HasColumnName("title");
                entity.Property(e => e.Content).HasColumnName("content");
                entity.Property(e => e.Created).HasColumnName("created");
                entity.Property(e => e.Modified).HasColumnName("modified");
            });

            // Реакция
            modelBuilder.Entity<Reaction>(entity =>
            {
                entity.ToTable("tbl_reaction");
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.TopicId).HasColumnName("topic_id");
                entity.Property(e => e.Content).HasColumnName("content");
                
            });

            //Тег
            modelBuilder.Entity<Tag>(entity =>
            {
                entity.ToTable("tbl_tag");
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Name).HasColumnName("name");
            });

<<<<<<< HEAD
            // Для промежуточной таблицы TopicTag
=======
            // Для промежуточной таблицы TopicTag (если нужен префикс)
>>>>>>> upstream/main
            modelBuilder.Entity<TopicTag>().ToTable("tbl_topic_tag");
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
                .OnDelete(DeleteBehavior.Cascade); 

            modelBuilder.Entity<TopicTag>()
                .HasOne(tt => tt.Tag)
                .WithMany(t => t.TopicTags)
                .HasForeignKey(tt => tt.TagId)
                .OnDelete(DeleteBehavior.Cascade);
            

            base.OnModelCreating(modelBuilder);
        }
    }
}