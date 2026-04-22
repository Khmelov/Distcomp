package com.sergey.orsik.repository;

import com.sergey.orsik.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreatorRepository extends JpaRepository<Creator, Long>, JpaSpecificationExecutor<Creator> {
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Long id);
}
