package by.bsuir.rest.service;

import by.bsuir.rest.cache.ResponseCache;
import by.bsuir.rest.dto.request.UserRequestTo;
import by.bsuir.rest.dto.response.UserResponseTo;
import by.bsuir.rest.entity.User;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.mapper.UserMapper;
import by.bsuir.rest.repository.PageQuery;
import by.bsuir.rest.repository.PageResult;
import by.bsuir.rest.repository.UserFilter;
import by.bsuir.rest.repository.UserRepository;
import by.bsuir.rest.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UserServiceCacheTest {

    @Mock
    private RequestValidator validator;

    @Test
    void repeatedReadsStayInCacheUntilWrite() {
        CountingUsers repository = new CountingUsers();
        UserService service = new UserServiceImpl(
                repository,
                new PlainUserMapper(),
                validator,
                new UpdateIdResolver(),
                ResponseCache.memory(new ObjectMapper()));

        UserRequestTo request = user("sergey@example.com", "Сергей");
        UserResponseTo created = service.create(request);

        assertThat(service.getById(created.getId()).getFirstname()).isEqualTo("Сергей");
        assertThat(service.getById(created.getId()).getLogin()).isEqualTo("sergey@example.com");
        assertThat(repository.readsById).isZero();

        int listsAfterFirst = repository.listReads;
        assertThat(service.getAll()).extracting(UserResponseTo::getId).containsExactly(created.getId());
        assertThat(service.getAll()).hasSize(1);
        assertThat(repository.listReads).isEqualTo(listsAfterFirst + 1);

        UserRequestTo update = user("sergey@example.com", "Ян");
        update.setId(created.getId());
        assertThat(service.update(created.getId(), update).getFirstname()).isEqualTo("Ян");
        assertThat(service.getById(created.getId()).getFirstname()).isEqualTo("Ян");
        assertThat(repository.readsById).isEqualTo(1);

        service.delete(created.getId());
        assertThatThrownBy(() -> service.getById(created.getId())).isInstanceOf(ApiException.class);
        assertThat(repository.readsById).isEqualTo(2);
    }

    private static UserRequestTo user(String login, String firstname) {
        UserRequestTo request = new UserRequestTo();
        request.setLogin(login);
        request.setPassword("password123");
        request.setFirstname(firstname);
        request.setLastname("Залужный");
        return request;
    }

    private static final class CountingUsers implements UserRepository {

        private final ConcurrentMap<Long, User> rows = new ConcurrentHashMap<>();
        private final AtomicLong ids = new AtomicLong();
        private int readsById;
        private int listReads;

        @Override
        public User save(User entity) {
            if (entity.getId() == null) {
                entity.setId(ids.incrementAndGet());
            }
            rows.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<User> findById(Long id) {
            readsById++;
            return Optional.ofNullable(rows.get(id));
        }

        @Override
        public boolean existsById(Long id) {
            return rows.containsKey(id);
        }

        @Override
        public boolean deleteById(Long id) {
            return rows.remove(id) != null;
        }

        @Override
        public PageResult<User> find(UserFilter filter, PageQuery pageQuery) {
            listReads++;
            return new PageResult<>(new ArrayList<>(rows.values()), 0, rows.size(), rows.size());
        }
    }

    private static final class PlainUserMapper implements UserMapper {

        @Override
        public User toEntity(UserRequestTo request) {
            User user = new User();
            user.setLogin(request.getLogin());
            user.setPassword(request.getPassword());
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            return user;
        }

        @Override
        public UserResponseTo toResponse(User user) {
            UserResponseTo response = new UserResponseTo();
            response.setId(user.getId());
            response.setLogin(user.getLogin());
            response.setPassword(user.getPassword());
            response.setFirstname(user.getFirstname());
            response.setLastname(user.getLastname());
            return response;
        }

        @Override
        public void update(UserRequestTo request, User user) {
            user.setLogin(request.getLogin());
            user.setPassword(request.getPassword());
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
        }
    }
}
