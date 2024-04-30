using AutoMapper;
using RV.Models;
using RV.Repositories;
using RV.Views.DTO;

namespace RV.Services.DataProviderServices.SQL
{
    public class SQLStickerDataProvider : IStickerDataProvider
    {
        private IRepository<Sticker> _repository;
        private IMapper _mapper;

        public SQLStickerDataProvider(IRepository<Sticker> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }
        public StickerDTO CreateSticker(StickerAddDTO item)
        {
            Sticker s = _mapper.Map<Sticker>(item);
            var res = _repository.Create(s);
            return _mapper.Map<StickerDTO>(res);
        }

        public int DeleteSticker(int id)
        {
            int res = _repository.Delete(id);
            return res;
        }

        public StickerDTO GetSticker(int id)
        {
            return _mapper.Map<StickerDTO>(_repository.Get(id));
        }

        public List<StickerDTO> GetStickers()
        {
            List<StickerDTO> res = [];
            foreach (Sticker s in _repository.GetAll())
            {
                res.Add(_mapper.Map<StickerDTO>(s));
            }
            return res;
        }

        public StickerDTO UpdateSticker(StickerUpdateDTO item)
        {
            var n = _mapper.Map<Sticker>(item);
            var res = _repository.Update(n);
            return _mapper.Map<StickerDTO>(res);
        }
    }
}
