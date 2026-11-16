package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.User;
import by.bsuir.rest.repository.UserFilter;
import by.bsuir.rest.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends JpaEntityRepository<User, UserFilter> implements UserRepository {

    public UserRepositoryImpl(UserJpaRepository jpa) {
        super(jpa);
    }

    @Override
    protected Specification<User> specification(UserFilter filter) {
        return EntitySpecifications.users(filter);
    }
}
