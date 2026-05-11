using System.Diagnostics;
using System.Net;
using Cassandra;
using Cassandra.Mapping;
using Confluent.Kafka;
using Newtonsoft.Json;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using Microsoft.EntityFrameworkCore;
using ISession = Cassandra.ISession;

namespace lab_1.Services;

public class CommentService : IAsyncService<CommentRequestDto, CommentResponseDto>
{
    private ISession _context;
    private Mapper mapper;
    private long _count;
    private BaseRequest<TblComment, CommentRequestDto> _request;
    private BaseResponse<CommentResponseDto?, TblComment> _response;


    public CommentService()
    {
        _context = Cluster.Builder().AddContactPoint("localhost").WithPort(55001).Build().Connect("distcomp");
        _context.Execute(
            "CREATE  TABLE if not exists tbl_comments (country text,storyId  bigint,id bigint, content text, primary key ((country), id));");
        _request = new();
        _response = new();
        mapper = new (_context);
        _count = _context.Execute("select count(*) from tbl_comments").FirstOrDefault().GetValue<long>(0);

    }



    public async Task<CommentResponseDto?> CreateAsync(CommentRequestDto dto)
    {
        dto.Id =_count ;
        var entity = _request.FromDto(dto);
        var message = JsonConvert.SerializeObject(entity);
        await SendOrderRequest("inTopic", message, 0);
        return _response.ToDto(entity);
    }

    public async Task<CommentResponseDto> ReadAsync(long id)
    {
        await SendOrderRequest("inTopic", JsonConvert.SerializeObject(id), 1);
        var res = GetRequest();
        return res == String.Empty ? null : _response.ToDto(JsonConvert.DeserializeObject<TblComment>(res));
    }

    public async Task<bool> DeleteAsync(long id)
    {
        return await SendOrderRequest("inTopic", JsonConvert.SerializeObject(id), 2);
    }

    public async Task<IEnumerable<CommentResponseDto>> GetAllAsync()
    {
         await SendOrderRequest("inTopic", null, 4);
         var res = JsonConvert.DeserializeObject<IEnumerable<TblComment>>(GetRequest());
         List<CommentResponseDto> result = new List<CommentResponseDto>();
         foreach (var no in res)
         {
             result.Add(_response.ToDto(no));
         }

         return result;
    }

    public async Task<CommentResponseDto> UpdateAsync(CommentRequestDto dto)
    {
        var entity = _request.FromDto(dto);
        await SendOrderRequest("inTopic", JsonConvert.SerializeObject(entity), 3);
        return _response.ToDto(JsonConvert.DeserializeObject<TblComment>(GetRequest()));
    }

    public CommentResponseDto? Update(CommentRequestDto dto)
    {
        var entity = _request.FromDto(dto);
        mapper.Update(entity);
        return _response.ToDto(entity);
    }
    
    

    public bool Delete(long id)
    {
        mapper.Delete(mapper.Single<TblComment>("where id = ? and country = \'Belarus\' ", id));
        _count--;
        return true;
    }

    public IEnumerable<CommentResponseDto?> GetAll()
    {
            foreach (var entity in mapper.Fetch<TblComment>())
            {
                yield return _response.ToDto(entity);
            }
    }
    private async Task <bool> SendOrderRequest
        (string topic, string message,int key) {
        ProducerConfig config = new ProducerConfig {
            BootstrapServers = "localhost:9092",
            ClientId = Dns.GetHostName()
        };

        try {
            using(var producer = new ProducerBuilder
                      <int, string> (config).Build()) {
                var result = await producer.ProduceAsync
                (topic, new Message <int, string> {
                    Key = key,Value = message
                });

                Debug.WriteLine($"Delivery Timestamp:{result.Timestamp.UtcDateTime}");
                return await Task.FromResult(true);
            }
        } catch (Exception ex) {
            Console.WriteLine($"Error occured: {ex.Message}");
        }

        return await Task.FromResult(false);
    }

    private string GetRequest()
    {
        var conf = new ConsumerConfig
        {
            GroupId = "test1",
            BootstrapServers = "localhost:9092",
            AutoOffsetReset = AutoOffsetReset.Earliest
        };
        var consumer = new ConsumerBuilder<Null, string>(conf).Build();
        
        consumer.Subscribe("outTopic");
        
        Stopwatch sw = new Stopwatch();
        sw.Start();
        var cancel = new CancellationToken();
        while (sw.ElapsedMilliseconds < 1100)
        {
            try
            {
                var cr = consumer.Consume(cancel);

                return cr.Message.Value;

            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (ConsumeException e)
            {
                // Consumer errors should generally be ignored (or logged) unless fatal.
                Console.WriteLine($"Consume error: {e.Error.Reason}");

                if (e.Error.IsFatal)
                {
                    // https://github.com/edenhill/librdkafka/blob/master/INTRODUCTION.md#fatal-consumer-errors
                    break;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"Unexpected error: {e}");
                break;
            }
           
        }

        return String.Empty;
    }
}