using Dapper;
using Npgsql;
using Publisher.Exceptions;
using Publisher.Mapper;
using Publisher.Model;
using Publisher.Repository.Params;
using System.Data;
using System.Reflection;
using System.Text;
using System.Text.Json;

namespace Publisher.Repository {
    public abstract class PostgresRepository<T> : IRepository<T> where T : BaseEntity {
        protected string _connectionString;
        protected string _tableName;
        protected ILogger<PostgresRepository<T>> _logger;
        protected ColumnMapper _columnMapper = new();
        public string GetConnectionString() => _connectionString;

        protected PostgresRepository(IConfiguration configuration, ILogger<PostgresRepository<T>> logger) {
            _connectionString = configuration.GetConnectionString("DefaultConnection")
                ?? throw new InvalidOperationException("Connection string not found");
            _logger = logger;
            _tableName = $"tbl_{typeof(T).Name.ToLower()}";
        }

        public async Task<T?> GetByIdAsync(long id) {
            const string sql = "SELECT * FROM {0} WHERE id = @id";
            using var connection = new NpgsqlConnection(_connectionString);

            try {
                return await connection.QueryFirstOrDefaultAsync<T>(
                    string.Format(sql, _tableName),
                    new { id });
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error getting entity by id {Id}", id);
                throw;
            }
        }

        public async Task<IEnumerable<T>> GetAllAsync() {
            const string sql = "SELECT * FROM {0}";
            using var connection = new NpgsqlConnection(_connectionString);

            try {
                return await connection.QueryAsync<T>(string.Format(sql, _tableName));
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error getting all entities");
                throw;
            }
        }

        public async Task<T> AddAsync(T entity) {
            _logger.LogInformation($"=== ADDING {typeof(T).Name} TO DATABASE ===");

            if (entity is Tweet tweet) {
                _logger.LogInformation($"Tweet before insert - ID: {tweet.Id}, EditorId: {tweet.EditorId}, Title: {tweet.Title}");
            }

            var properties = GetMappedPropertiesForInsert(entity);

            _logger.LogDebug($"Mapped properties for insert:");
            foreach (var prop in properties) {
                _logger.LogDebug($"  {prop.PropertyName} -> {prop.ColumnName} = {prop.Value} (type: {prop.Value?.GetType()})");
            }

            var columnNames = string.Join(", ", properties.Select(p => p.ColumnName));
            var parameterNames = string.Join(", ", properties.Select(p => "@" + p.PropertyName));

            var sql = $"INSERT INTO {_tableName} ({columnNames}) VALUES ({parameterNames}) RETURNING *";

            _logger.LogDebug($"SQL: {sql}");

            using var connection = new NpgsqlConnection(_connectionString);

            try {
                var parameters = new DynamicParameters();
                foreach (var prop in properties) {
                    _logger.LogDebug($"Adding parameter: @{prop.PropertyName} = {prop.Value} (type: {prop.Value?.GetType()})");
                    parameters.Add(prop.PropertyName, prop.Value);
                }

                _logger.LogDebug($"Executing query with parameters...");

                var result = await connection.QuerySingleAsync<T>(sql, parameters);

                if (result is Tweet resultTweet) {
                    _logger.LogInformation($"Tweet after insert - ID: {resultTweet.Id}, EditorId: {resultTweet.EditorId}");
                }

                _logger.LogInformation($"=== SUCCESSFULLY ADDED {typeof(T).Name} WITH ID: {result.Id} ===");
                return result;
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error adding entity");
                throw;
            }
        }

        private List<(string PropertyName, string ColumnName, object? Value)> GetMappedPropertiesForInsert(T entity) {
            var result = new List<(string, string, object?)>();
            var _columnMappings = _columnMapper._columnMappings[entity.GetType()];

            foreach (var kv in _columnMappings) {
                var propertyName = kv.Key;
                var columnName = kv.Value;

                if (propertyName.Equals("Id", StringComparison.OrdinalIgnoreCase))
                    continue;

                var property = typeof(T).GetProperty(propertyName);
                if (property != null) {
                    var value = property.GetValue(entity);

                    if (propertyName.Equals("EditorId", StringComparison.OrdinalIgnoreCase)) {
                        _logger.LogDebug($"EditorId value being mapped: {value} (type: {value?.GetType()})");
                    }

                    result.Add((propertyName, columnName, value));
                }
            }

            return result;
        }

        private List<(string PropertyName, string ColumnName, object? Value)> GetMappedProperties(T entity) {
            var result = new List<(string, string, object?)>();
            var _columnMappings = _columnMapper._columnMappings[entity.GetType()];

            _logger.LogDebug($"Getting mapped properties for {typeof(T).Name}");
            _logger.LogDebug($"Column mappings: {JsonSerializer.Serialize(_columnMappings)}");

            foreach (var kv in _columnMappings) {
                var propertyName = kv.Key;
                var columnName = kv.Value;

                if (propertyName.Equals("Id", StringComparison.OrdinalIgnoreCase)) {
                    _logger.LogDebug($"Skipping Id property");
                    continue;
                }

                var property = typeof(T).GetProperty(propertyName);
                if (property != null) {
                    var value = property.GetValue(entity);
                    _logger.LogDebug($"Found property: {propertyName} = {value} -> column: {columnName}");
                    result.Add((propertyName, columnName, value));
                }
                else {
                    _logger.LogWarning($"Property {propertyName} not found on type {typeof(T).Name}");
                }
            }

            return result;
        }


        public async Task<T> UpdateAsync(T entity) {
            _logger.LogInformation($"Updating {typeof(T).Name} with ID: {entity.Id}");

            var properties = GetMappedProperties(entity);

            if (!properties.Any()) {
                throw new InvalidOperationException($"No mapped properties found for {typeof(T).Name}");
            }

            var setClause = string.Join(", ", properties.Select(p => $"{p.ColumnName} = @{p.PropertyName}"));
            var sql = $"UPDATE {_tableName} SET {setClause} WHERE id = @Id RETURNING *";

            _logger.LogDebug($"Update SQL: {sql}");

            using var connection = new NpgsqlConnection(_connectionString);

            try {
                var parameters = new DynamicParameters();

                parameters.Add("Id", entity.Id);

                foreach (var prop in properties) {
                    parameters.Add(prop.PropertyName, prop.Value);
                }

                var result = await connection.QuerySingleAsync<T>(sql, parameters);
                _logger.LogInformation($"Successfully updated {typeof(T).Name} with ID: {result.Id}");
                return result;
            }
            catch (PostgresException ex) {
                _logger.LogError(ex, "Error updating entity with id {Id}", entity.Id);
                throw;
            }
        }

        public async Task<bool> DeleteAsync(long id) {
            const string sql = "DELETE FROM {0} WHERE id = @id";
            using var connection = new NpgsqlConnection(_connectionString);

            try {
                var affectedRows = await connection.ExecuteAsync(string.Format(sql, _tableName), new { id });
                return affectedRows > 0;
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error deleting entity with id {Id}", id);
                throw;
            }
        }

        public async Task<bool> ExistsAsync(long id) {
            const string sql = "SELECT COUNT(1) FROM {0} WHERE id = @id";
            using var connection = new NpgsqlConnection(_connectionString);

            try {
                var count = await connection.ExecuteScalarAsync<int>(string.Format(sql, _tableName), new { id });
                return count > 0;
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error checking if entity exists with id {Id}", id);
                throw;
            }
        }

        public async Task<PagedResponse<T>> GetPagedAsync(QueryParams queryParams) {
            var sql = $@"
            WITH filtered AS (
                SELECT * FROM {_tableName}
                {BuildWhereClause(queryParams)}
            )
            SELECT 
                (SELECT COUNT(*) FROM filtered) as TotalCount,
                (SELECT json_agg(row_to_json(t)) FROM (
                    SELECT * FROM filtered
                    {BuildOrderByClause(queryParams)}
                    LIMIT @PageSize OFFSET @Offset
                ) t) as Items";

            using var connection = new NpgsqlConnection(_connectionString);

            var offset = (queryParams.PageNumber - 1) * queryParams.PageSize;

            var result = await connection.QuerySingleAsync<(long TotalCount, string Items)>(sql, new {
                PageSize = queryParams.PageSize,
                Offset = offset
            });

            return new PagedResponse<T> {
                Items = JsonSerializer.Deserialize<List<T>>(result.Items) ?? new List<T>(),
                PageNumber = queryParams.PageNumber,
                PageSize = queryParams.PageSize,
                TotalCount = result.TotalCount
            };
        }

        public async Task<IEnumerable<T>> FindAsync(FilterCriteria<T> filter) {
            string condition = filter.Operator switch {
                FilterOperator.Equals => $"{filter.Field} = @Value",
                FilterOperator.Contains => $"{filter.Field} ILIKE '%' || @Value || '%'",
                FilterOperator.GreaterThan => $"{filter.Field} > @Value",
                FilterOperator.LessThan => $"{filter.Field} < @Value",
                _ => "1=1"
            };

            var sql = $"SELECT * FROM {_tableName} WHERE {condition}";

            using var connection = new NpgsqlConnection(_connectionString);

            try {
                return await connection.QueryAsync<T>(sql, new { filter.Value });
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error finding entities with filter");
                throw;
            }
        }

        public async Task<IEnumerable<T>> GetSortedAsync(string sortBy, string sortOrder = "asc") {
            var sql = $"SELECT * FROM {_tableName} ORDER BY {sortBy} {sortOrder}";

            using var connection = new NpgsqlConnection(_connectionString);

            try {
                return await connection.QueryAsync<T>(sql);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error getting sorted entities");
                throw;
            }
        }

        protected virtual string BuildWhereClause(QueryParams queryParams) {
            return "";
        }

        protected virtual string BuildOrderByClause(QueryParams queryParams) {
            if (string.IsNullOrEmpty(queryParams.SortBy))
                return "";

            var order = queryParams.SortOrder?.ToLower() == "desc" ? "DESC" : "ASC";
            return $"ORDER BY {queryParams.SortBy} {order}";
        }
    }
}