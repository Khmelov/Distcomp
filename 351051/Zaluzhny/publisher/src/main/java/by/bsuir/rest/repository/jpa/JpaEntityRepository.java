package by.bsuir.rest.repository.jpa;

import by.bsuir.rest.entity.Identifiable;
import by.bsuir.rest.repository.EntityRepository;
import by.bsuir.rest.repository.PageQuery;
import by.bsuir.rest.repository.PageResult;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public abstract class JpaEntityRepository<T extends Identifiable, F> implements EntityRepository<T, F> {

    private final JpaRepository<T, Long> jpa;
    private final JpaSpecificationExecutor<T> specifications;

    protected <R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>> JpaEntityRepository(R repository) {
        this.jpa = repository;
        this.specifications = repository;
    }

    protected abstract Specification<T> specification(F filter);

    @Override
    public T save(T entity) {
        return jpa.saveAndFlush(entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpa.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && jpa.existsById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        jpa.flush();
        return true;
    }

    @Override
    public PageResult<T> find(F filter, PageQuery pageQuery) {
        PageQuery query = pageQuery == null ? PageQuery.all() : pageQuery;
        return PageResult.of(specifications.findAll(specification(filter), query.toPageable()));
    }
}
