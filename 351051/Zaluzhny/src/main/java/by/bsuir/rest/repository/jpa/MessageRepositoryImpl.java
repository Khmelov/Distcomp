package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Message;
import by.bsuir.rest.repository.MessageFilter;
import by.bsuir.rest.repository.MessageRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepositoryImpl extends JpaEntityRepository<Message, MessageFilter> implements MessageRepository {

    public MessageRepositoryImpl(MessageJpaRepository jpa) {
        super(jpa);
    }

    @Override
    protected Specification<Message> specification(MessageFilter filter) {
        return EntitySpecifications.messages(filter);
    }
}
