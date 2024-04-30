using Cassandra;
using Cassandra.Mapping;
using Discussion.Models;
using System.Diagnostics.Metrics;

namespace Discussion.Repositories.SQLRepositories
{
    public class NoSQLNoteRepository : IRepository<Note>
    {
        private readonly Cluster _cluster;
        private readonly Cassandra.ISession _session;

        public NoSQLNoteRepository()
        {
            _cluster = Cluster.Builder().AddContactPoint("localhost").Build();
            try
            {
                _session = _cluster.Connect("distcomp");
            } catch (InvalidQueryException )
            {
                using (var session = _cluster.Connect())
                {
                    session.Execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}");
                    session.Execute("CREATE TABLE IF NOT EXISTS distcomp.tbl_note (country text,tweetId int,id int,content text,PRIMARY KEY ((country), tweetId, id))");
                }
            }
            _session = _cluster.Connect("distcomp");
        }

        public void Create(Note entity)
        {
            var mapper = new Mapper(_session);
            mapper.Insert(entity);
        }

        public Note Get(int id)
        {
            var mapper = new Mapper(_session);
            return mapper.FirstOrDefault<Note>($"WHERE id = {id} ALLOW FILTERING");
        }

        public void Update(Note entity)
        {
            var n = Get(entity.id);
            entity.country = n.country;
            var mapper = new Mapper(_session);
            mapper.Update(entity);
        }

        public int Delete(int id)
        {
            var n = Get(id);
            var mapper = new Mapper(_session);
            if (n != null)
            {
                mapper.Delete<Note>(n);
                return 1;
            }
            return 0;            
        }

        public List<Note> GetAll()
        {
            var mapper = new Mapper(_session);
            return mapper.Fetch<Note>().ToList();
        }
    }
}
