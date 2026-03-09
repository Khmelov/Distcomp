using Application.Repository;
using Cassandra;
using Cassandra.Mapping;
using Domain.Models;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Infrastructe.ProjectCassandra
{
    public class CassandraRepository : IRepository<Comment>
    {
        private readonly Cassandra.ISession _session;
        private readonly Cassandra.Mapping.IMapper _mapper;

        public CassandraRepository(Cassandra.ISession session)
        {
            _session = session;

            // 1. Создаем таблицу (bigint соответствует long в C#)
            _session.Execute(@"
                CREATE TABLE IF NOT EXISTS tbl_comments (
                    id bigint PRIMARY KEY, 
                    issueid bigint, 
                    content text
                )");

            // 2. Настраиваем маппинг
            var config = new MappingConfiguration().Define(
                new Map<Comment>()
                    .TableName("tbl_comments")
                    .PartitionKey(c => c.id)
                    .Column(c => c.id, cm => cm.WithName("id"))
                    .Column(c => c.issueId, cm => cm.WithName("issueid"))
                    .Column(c => c.content, cm => cm.WithName("content"))
                    .Column(c => c.issue, cm => cm.Ignore()) // Игнорируем навигационное свойство
            );

            _mapper = new Cassandra.Mapping.Mapper(_session, config);
        }

        public async Task<Comment?> GetByIdAsync(long id)
        {
            // Поиск по Primary Key
            return await _mapper.SingleOrDefaultAsync<Comment>("WHERE id = ?", id);
        }

        public async Task<IList<Comment>> GetAllAsync()
        {
            // Маппер автоматически заполнит список объектов Comment
            var comments = await _mapper.FetchAsync<Comment>("SELECT id, issueid, content FROM comments");
            return comments.ToList();
        }

        public async Task AddAsync(Comment comment)
        {
            // В Cassandra это INSERT. Если ID совпадет, данные перезапишутся (Upsert)
            await _mapper.InsertAsync(comment);
        }

        public async Task UpdateAsync(Comment comment)
        {
            // Обновление всего объекта
            await _mapper.UpdateAsync(comment);
        }

        public async Task<Comment?> UpdateAsync(long id, Comment editor)
        {
            // 1. Проверяем, существует ли запись
            var existingComment = await GetByIdAsync(id);
            if (existingComment == null)
            {
                return null;
            }

            // 2. Обновляем поля (кроме ID, так как это часть первичного ключа)
            existingComment.issueId = editor.issueId;
            existingComment.content = editor.content;

            // 3. Сохраняем изменения
            await _mapper.UpdateAsync(existingComment);

            return existingComment;
        }

        public async Task<int> DeleteAsync(long id)
        {
            // Удаление в Cassandra всегда успешно (даже если записи нет), 
            // поэтому просто возвращаем 1
            var statement = new SimpleStatement("DELETE FROM comments WHERE id = ?", id);
            await _session.ExecuteAsync(statement);
            return 1;
        }
    }
}