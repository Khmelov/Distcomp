using Confluent.Kafka;
using Newtonsoft.Json;
using RV.Services.Remote;
using RV.Views.DTO;
using System.Collections.Generic;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace RV.Services.DataProviderServices.Remote
{
    public class KafkaNoteDataProvider : INoteDataProvider
    {
        private ConsumerConfig config = new ConsumerConfig
        {
            BootstrapServers = "localhost:9092",
            GroupId = "foo",
            AutoOffsetReset = AutoOffsetReset.Earliest
        };
        private CancellationTokenSource cts = new CancellationTokenSource();

        public NoteDTO CreateNote(NoteAddDTO item)
        {
            var rand = new Random();
            item.id = rand.Next();
            Produce(item.tweetId.ToString(), "create", JsonConvert.SerializeObject(item));
            var res = new NoteDTO()
            {
                content = item.content,
                id = item.id,
                tweetId = item.tweetId,
                country = "by",
            };
            return res;
        }

        public int DeleteNote(int id)
        {
            var data = new Dictionary<string, int>(){
                { "id", id }
            };
            Produce("extra", "delete", JsonConvert.SerializeObject(data));
            var resString = Consume();
            int res;
            if (resString != null)
            {
                var temp = JsonConvert.DeserializeObject<KafkaMessageTemplate>(resString);
                res = int.Parse(temp.Data);
            }
            else
                throw new Exception("tested");
            return res;
        }

        public NoteDTO GetNote(int id)
        {
            var data = new Dictionary<string, int>(){
                { "id", id }
            };
            Produce("extra", "get", JsonConvert.SerializeObject(data));
            var resString = Consume();
            NoteDTO res;
            if (resString != null)
            {
                var temp = JsonConvert.DeserializeObject<KafkaMessageTemplate>(resString);
                res = JsonConvert.DeserializeObject<NoteDTO>(temp.Data);
            }
            else
                throw new Exception("tested");
            return res;
        }

        public List<NoteDTO> GetNotes()
        {
            Produce("extra", "getAll", "");
            var resString = Consume();
            List<NoteDTO> res;
            if (resString != null)
            {
                var temp = JsonConvert.DeserializeObject<KafkaMessageTemplate>(resString);
                res = JsonConvert.DeserializeObject<List<NoteDTO>>(temp.Data);
            }
            else
                throw new Exception("tested");
            return res;
        }

        public NoteDTO UpdateNote(NoteUpdateDTO item)
        {
            var key = item.tweetId != null ? item.tweetId.ToString().ToString() : "extra";
            Produce(key, "update", JsonConvert.SerializeObject(item));
            var resString = Consume();
            NoteDTO res;
            if (resString != null)
            {
                var temp = JsonConvert.DeserializeObject<KafkaMessageTemplate>(resString);
                res = JsonConvert.DeserializeObject<NoteDTO>(temp.Data);
            }
            else
                throw new Exception("tested");
            return res;
        }

        private string? Consume()
        { 
            using (var consumer = new ConsumerBuilder<Ignore, string>(config).Build())
            {
                consumer.Subscribe("OutTopic");
                try
                {
                    var consumeResult = consumer.Consume(cts.Token);
                    return consumeResult.Value;
                }
                finally
                {
                    consumer.Close();
                }
            }
            return null;
        }

        private void Produce(string key, string command, string data)
        {
            KafkaMessageTemplate template = new KafkaMessageTemplate();
            template.Command = command;
            template.Data = data;
            using (var producer = new ProducerBuilder<string, string>(config).Build())
            {
                producer.Produce("InTopic", new Message<string, string>
                {
                    Value = JsonConvert.SerializeObject(template),
                    Key = key,
                });
                producer.Flush();
            }
        }
    }
}
