using System.Text;
using Distcomp_NoteMicroservice.Model.NoteModel;

namespace Distcomp_NoteMicroservice.Repository.NoteRepos;
using Cassandra;
using Cassandra.Mapping;
using Microsoft.Extensions.Logging;

public class NoteRepository : INoteRepository
{
    private readonly IMapper _mapper;
    private readonly ILogger<NoteRepository> _logger;

    
    private const int DefaultLimit = 100;
    private const int MaxLimit = 1000;
    private const ConsistencyLevel DefaultConsistency = ConsistencyLevel.LocalQuorum;

    public NoteRepository(
        ISession session, 
        ILogger<NoteRepository> logger)
    {
        _logger = logger;

        
        var mappingConfig = new MappingConfiguration()
            .Define(new Map<Note>()
                .TableName("tbl_notes")
                .PartitionKey(n => n.Country)
                .ClusteringKey(n => n.TopicId, SortOrder.Unspecified)
                .ClusteringKey(n => n.Id, SortOrder.Ascending)
                .Column(n => n.Country, cm => cm.WithName("country"))
                .Column(n => n.TopicId, cm => cm.WithName("topic_id"))
                .Column(n => n.Id, cm => cm.WithName("id"))
                .Column(n => n.Content, cm => cm.WithName("content"))
                .Column(n => n.CreatedAt, cm => cm.WithName("created_at"))
                .Column(n => n.UpdatedAt, cm => cm.WithName("updated_at"))
            );

        _mapper = new Mapper(session, mappingConfig);
    }

    public async Task<Note?> GetNoteByIdAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogDebug(
            "Fetching note: country={Country}, topicId={TopicId}, id={Id}",
            country, topicId, id);

        try
        {
            
            var queryOptions = new CqlQueryOptions()
                .SetConsistencyLevel(DefaultConsistency);

            
            var cqlQuery = new Cql(
                "SELECT * FROM tbl_notes WHERE country = ? AND topic_id = ? AND id = ?",
                queryOptions,
                country, topicId, id);

            
            var note = await _mapper.FirstOrDefaultAsync<Note>(cqlQuery);

            if (note == null)
            {
                _logger.LogDebug(
                    "Note not found: country={Country}, topicId={TopicId}, id={Id}",
                    country, topicId, id);
            }

            return note;
        }
        catch (OperationCanceledException)
        {
            _logger.LogWarning(
                "Request cancelled: country={Country}, topicId={TopicId}, id={Id}",
                country, topicId, id);
            throw;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error fetching note: country={Country}, topicId={TopicId}, id={Id}",
                country, topicId, id);
            throw;
        }
    }

    public async Task<IEnumerable<Note>> GetNotesByCountryAndTopicAsync(
        string country, 
        long topicId,
        long? minId = null,
        long? maxId = null,
        int limit = DefaultLimit,
        CancellationToken cancellationToken = default)
    {
        limit = Math.Clamp(limit, 1, MaxLimit);

        _logger.LogDebug(
            "Fetching notes: country={Country}, topicId={TopicId}, minId={MinId}, maxId={MaxId}, limit={Limit}",
            country, topicId, minId, maxId, limit);

        try
        {
            
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM tbl_notes WHERE country = ? AND topic_id = ?");
            
            List<object> parameters = new List<object> { country, topicId };

            if (minId.HasValue)
            {
                queryBuilder.Append(" AND id >= ?");
                parameters.Add(minId.Value);
            }

            if (maxId.HasValue)
            {
                queryBuilder.Append(" AND id <= ?");
                parameters.Add(maxId.Value);
            }

            queryBuilder.Append(" LIMIT ?");
            parameters.Add(limit);

            
            CqlQueryOptions? queryOptions = new CqlQueryOptions()
                .SetConsistencyLevel(DefaultConsistency)
                .SetPageSize(limit);

            
            Cql cqlQuery = new Cql(
                queryBuilder.ToString(),
                queryOptions,
                parameters.ToArray());

            
            IEnumerable<Note>? notes = await _mapper.FetchAsync<Note>(cqlQuery);

            return notes.ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error fetching notes: country={Country}, topicId={TopicId}",
                country, topicId);
            throw;
        }
    }

    public async Task<IEnumerable<Note>> GetNotesByCountryAndTopicRangeAsync(
        string country, 
        long minTopicId, 
        long maxTopicId,
        int limit = DefaultLimit,
        CancellationToken cancellationToken = default)
    {
        limit = Math.Clamp(limit, 1, MaxLimit);

        _logger.LogDebug(
            "Fetching notes: country={Country}, minTopicId={MinTopicId}, maxTopicId={MaxTopicId}, limit={Limit}",
            country, minTopicId, maxTopicId, limit);

        try
        {
            
            CqlQueryOptions? queryOptions = new CqlQueryOptions()
                .SetConsistencyLevel(DefaultConsistency)
                .SetPageSize(limit);

            
            Cql cqlQuery = new Cql(
                "SELECT * FROM tbl_notes WHERE country = ? AND topic_id >= ? AND topic_id <= ? LIMIT ?",
                queryOptions,
                country, minTopicId, maxTopicId, limit);

            IEnumerable<Note>? notes = await _mapper.FetchAsync<Note>(cqlQuery);

            return notes.ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error fetching notes by range: country={Country}, minTopicId={Min}, maxTopicId={Max}",
                country, minTopicId, maxTopicId);
            throw;
        }
    }

    public async Task<Note> CreateAsync(
        Note note, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation(
            "Creating note: country={Country}, topicId={TopicId}, id={Id}",
            note.Country, note.TopicId, note.Id);

        try
        {
            note.CreatedAt = DateTimeOffset.UtcNow;
            note.UpdatedAt = null;
            
            await _mapper.InsertAsync(note);

            return note;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error creating note: country={Country}, topicId={TopicId}, id={Id}",
                note.Country, note.TopicId, note.Id);
            throw;
        }
    }

    public async Task<Note> UpdateAsync(
        Note note, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation(
            "Updating note: country={Country}, topicId={TopicId}, id={Id}",
            note.Country, note.TopicId, note.Id);

        try
        {
            note.UpdatedAt = DateTimeOffset.UtcNow;

            
            await _mapper.UpdateAsync(note);

            return note;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error updating note: country={Country}, topicId={TopicId}, id={Id}",
                note.Country, note.TopicId, note.Id);
            throw;
        }
    }

    public async Task<bool> DeleteAsync(
        string country, 
        long topicId, 
        long id, 
        CancellationToken cancellationToken = default)
    {
        _logger.LogInformation(
            "Deleting note: country={Country}, topicId={TopicId}, id={Id}",
            country, topicId, id);

        try
        {
            
            var existingNote = await GetNoteByIdAsync(country, topicId, id, cancellationToken);
            if (existingNote == null)
            {
                _logger.LogDebug(
                    "Note not found for deletion: country={Country}, topicId={TopicId}, id={Id}",
                    country, topicId, id);
                return false;
            }

            
            await _mapper.DeleteAsync(existingNote);

            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error deleting note: country={Country}, topicId={TopicId}, id={Id}",
                country, topicId, id);
            throw;
        }
    }
}