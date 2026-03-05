using AutoMapper;
using Project.Dto;
using Project.Model;
using Project.Repository;

namespace Project.Service {
    public class StickerService : BaseService<Sticker, StickerRequestTo, StickerResponseTo> {
        public StickerService(IRepository<Sticker> repository, IMapper mapper, ILogger<StickerService> logger)
            : base(repository, mapper, logger) {
        }
    }
}