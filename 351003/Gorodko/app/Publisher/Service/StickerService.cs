using AutoMapper;
using Publisher.Dto;
using Publisher.Model;
using Publisher.Repository;

namespace Publisher.Service {
    public class StickerService : BaseService<Sticker, StickerRequestTo, StickerResponseTo> {
        public StickerService(IRepository<Sticker> repository, IMapper mapper, ILogger<StickerService> logger)
            : base(repository, mapper, logger) {
        }
    }
}