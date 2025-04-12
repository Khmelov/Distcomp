using AutoMapper;
using DistComp.Data;
using DistComp.Models;
using Microsoft.EntityFrameworkCore;

namespace DistComp.Services.Implementation {
    public class UserService : ICommonAsyncService<long, User, UserInDto, UserOutDto> {

        private DCContext context;
        private IMapper mapper;

        public UserService(DCContext context, IMapper mapper) {
            this.context = context;
            this.mapper = mapper;
        }

        public async Task<IEnumerable<UserOutDto>> GetAll() {
            return (await context.Users.ToListAsync()).Select(e => mapper.Map<UserOutDto>(e));
        }

        public async Task<UserOutDto?> Get(long id) {
            var user = await context.Users.FindAsync(id);
            if (user == null) {
                return null;
            } else {
                return mapper.Map<UserOutDto>(user);
            }
        }

        public async Task<UserOutDto> Create(UserInDto data) {
            User newUser = mapper.Map<User>(data);
            await context.Users.AddAsync(newUser);
            await context.SaveChangesAsync();

            return mapper.Map<UserOutDto>(newUser);
        }

        public async Task<UserOutDto> Update(User data) {
            var user = await context.Users.FirstOrDefaultAsync(u => u.Id == data.Id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            user.Login = data.Login;
            user.Password = data.Password;
            user.Firstname = data.Firstname;
            user.Lastname = data.Lastname;
            await context.SaveChangesAsync();

            return mapper.Map<UserOutDto>(user);
        }

        public async Task Delete(long id) {
            var user = await context.Users.FirstOrDefaultAsync(u => u.Id == id)
                ?? throw new KeyNotFoundException("Entity with specified ID does not exist");

            context.Users.Remove(user);
            await context.SaveChangesAsync();
        }
    }
}
