using rest_api.Entities;
using rest_api.InMemory;
using rest_api.Dtos;

namespace rest_api.Services
{
    public class ReactionService : BaseService<Reaction, ReactionRequestTo, ReactionResponseTo>
    {
        public ReactionService(IRepository<Reaction> repository) : base(repository)
        {
        }

        public override ReactionResponseTo Create(ReactionRequestTo request)
        {
            var reaction = MapToEntity(request);
  

            _repository.Add(reaction);
            return MapToResponse(reaction);
        }

        public override ReactionResponseTo Update(ReactionRequestTo request)
        {
            var id = request.Id;
            var reaction = _repository.GetById(id);
            if (reaction == null)
                throw new KeyNotFoundException($"Reaction with id {id} not found");

            reaction.TopicId = request.TopicId;
            reaction.Content = request.Content;
            

            _repository.Update(reaction);
            return MapToResponse(reaction);
        }

        protected override ReactionResponseTo MapToResponse(Reaction entity)
        {
            return new ReactionResponseTo
            {
                Id = entity.Id,
                TopicId = entity.TopicId,
                Content = entity.Content,
                
            };
        }

        protected override Reaction MapToEntity(ReactionRequestTo request)
        {
            return new Reaction
            {
                TopicId = request.TopicId,
                Content = request.Content
            };
        }
    }
}