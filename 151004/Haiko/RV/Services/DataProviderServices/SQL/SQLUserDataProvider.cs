using AutoMapper;
using RV.Models;
using RV.Repositories;
using RV.Views.DTO;

namespace RV.Services.DataProviderServices.SQL
{
    public class SQLUserDataProvider : IUserDataProvider
    {
        private IRepository<User> _repository;
        private IMapper _mapper;

        public SQLUserDataProvider(IRepository<User> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }
        public UserDTO CreateUser(UserAddDTO item)
        {
            User u = _mapper.Map<User>(item);
            var res = _repository.Create(u);
            return _mapper.Map<UserDTO>(res);
        }

        public int DeleteUser(int id)
        {
            int res = _repository.Delete(id);
            return res;
        }

        public UserDTO GetUser(int id)
        {
            return _mapper.Map<UserDTO>(_repository.Get(id));
        }

        public List<UserDTO> GetUsers()
        {
            List<UserDTO> res = [];
            foreach (User u in _repository.GetAll())
            {
                res.Add(_mapper.Map<UserDTO>(u));
            }
            return res;
        }   

        public UserDTO UpdateUser(UserUpdateDTO item)
        {
            var n = _mapper.Map<User>(item);
            var res = _repository.Update(n);
            return _mapper.Map<UserDTO>(res);
        }
    }
}
