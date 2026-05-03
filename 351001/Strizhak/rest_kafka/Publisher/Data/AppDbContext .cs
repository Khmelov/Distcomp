using Microsoft.EntityFrameworkCore;
using Publisher.Entities;

namespace Publisher.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<User> Users { get; set; }
        public DbSet<Topic> Topics { get; set; }
       
        public DbSet<Tag> Tags { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.HasSequence<long>("tbl_reaction_id_seq", schema: "distcomp")
                .StartsAt(1)
                .IncrementsBy(1);
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

           

            //Тег
            modelBuilder.Entity<Tag>(entity =>
            {
                entity.ToTable("tbl_tag");
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Name).HasColumnName("name");
            });

            // Для промежуточной таблицы TopicTag (если нужен префикс)
            modelBuilder.Entity<TopicTag>().ToTable("tbl_topic_tag");
            // Связь Topic -> User
            modelBuilder.Entity<Topic>()
                .HasOne(t => t.User)
                .WithMany(u => u.Topics)
                .HasForeignKey(t => t.UserId);
                

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