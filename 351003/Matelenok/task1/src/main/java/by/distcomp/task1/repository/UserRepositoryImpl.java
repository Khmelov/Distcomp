package by.distcomp.task1.repository;

import by.distcomp.task1.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends CrudRepositoryImpl<User>
        implements UserRepository {

    @Override
    protected Long getId(User user) {
        return user.getId();
    }

    @Override
    protected void setId(User user, Long id) {
        user.setId(id);
    }
}