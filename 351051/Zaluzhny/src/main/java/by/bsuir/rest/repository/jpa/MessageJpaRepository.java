package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageJpaRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
}
