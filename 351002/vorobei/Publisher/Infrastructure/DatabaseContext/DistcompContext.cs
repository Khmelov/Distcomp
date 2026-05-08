using DataAccess.Models;
using Microsoft.EntityFrameworkCore;
using Infrastructure.DatabaseContext.Configurations;

namespace Infrastructure.DatabaseContext
{
    public class DistcompContext(DbContextOptions<DistcompContext> options) : DbContext(options)
    {
        public DbSet<Creator> tbl_creator { get; set; }
        public DbSet<Mark> tbl_mark { get; set; }
        public DbSet<Story> tbl_story { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.ApplyConfiguration(new CreatorConfiguration());
            modelBuilder.ApplyConfiguration(new MarkConfiguration());
            modelBuilder.ApplyConfiguration(new StoryConfiguration());
        }
    }
}
