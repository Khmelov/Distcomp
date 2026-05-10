using Publisher.Model;
using Publisher.Repository.Params;

namespace Publisher.Repository {
    public class EditorRepository : PostgresRepository<Editor>, IRepository<Editor> {
        public EditorRepository(IConfiguration configuration, ILogger<PostgresRepository<Editor>> logger)
            : base(configuration, logger) {
            _tableName = "tbl_editor";
        }

        protected override string BuildWhereClause(QueryParams queryParams) {
            if (queryParams is not SearchParams searchParams)
                return "";

            var conditions = new List<string>();

            if (!string.IsNullOrEmpty(searchParams.Login))
                conditions.Add("login ILIKE '%' || @Login || '%'");

            if (!string.IsNullOrEmpty(searchParams.Firstname))
                conditions.Add("firstname ILIKE '%' || @Firstname || '%'");

            if (!string.IsNullOrEmpty(searchParams.Lastname))
                conditions.Add("lastname ILIKE '%' || @Lastname || '%'");

            if (searchParams.Ids?.Any() == true)
                conditions.Add("id = ANY(@Ids)");

            return conditions.Any() ? "WHERE " + string.Join(" AND ", conditions) : "";
        }
    }
}