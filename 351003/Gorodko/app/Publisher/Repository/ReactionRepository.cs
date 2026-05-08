using Publisher.Model;

namespace Publisher.Repository {
    public class ReactionRepository : PostgresRepository<Reaction>, IRepository<Reaction> {
        public ReactionRepository(IConfiguration configuration, ILogger<PostgresRepository<Reaction>> logger)
            : base(configuration, logger) {
            _tableName = "tbl_reaction";
        }
    }
}