package com.example.rv.impl.issue;

import com.example.rv.api.repository.CrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueService {
    public final CrudRepository<Issue, Long> issueCrudRepository;
    public final IssueMapperImpl issueMapper;
}
