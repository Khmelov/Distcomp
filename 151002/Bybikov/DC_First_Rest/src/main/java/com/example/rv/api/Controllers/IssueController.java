package com.example.rv.api.Controllers;

import com.example.rv.impl.issue.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1.0")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    List<IssueResponseTo> getIssues() {
        return issueService.issueMapper.issueToResponseTo(issueService.issueCrudRepository.getAll());
    }

    @RequestMapping(value = "/issues", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    IssueResponseTo makeIssue(@RequestBody IssueRequestTo issueRequestTo) {

        var toBack = issueService.issueCrudRepository.save(
                issueService.issueMapper.dtoToEntity(issueRequestTo)
        );

        Issue issue = toBack.orElse(null);

        assert issue != null;
        return issueService.issueMapper.issueToResponseTo(issue);
    }

    @RequestMapping(value = "/issues/{id}", method = RequestMethod.GET)
    IssueResponseTo getIssue(@PathVariable Long id) {
        return issueService.issueMapper.issueToResponseTo(
                Objects.requireNonNull(issueService.issueCrudRepository.getById(id).orElse(null)));
    }

    @RequestMapping(value = "/issues", method = RequestMethod.PUT)
    IssueResponseTo updateIssue(@RequestBody IssueRequestTo issueRequestTo, HttpServletResponse response) {
        Issue issue = issueService.issueMapper.dtoToEntity(issueRequestTo);
        var newissue = issueService.issueCrudRepository.update(issue).orElse(null);
        if (newissue != null) {
            response.setStatus(200);
            return issueService.issueMapper.issueToResponseTo(newissue);
        } else{
            response.setStatus(403);
            return issueService.issueMapper.issueToResponseTo(issue);
        }
    }

    @RequestMapping(value = "/issues/{id}", method = RequestMethod.DELETE)
    int deleteIssue(@PathVariable Long id, HttpServletResponse response) {
        Issue issueToDelete = issueService.issueCrudRepository.getById(id).orElse(null);
        if (Objects.isNull(issueToDelete)) {
            response.setStatus(403);
        } else {
            issueService.issueCrudRepository.delete(issueToDelete);
            response.setStatus(204);
        }
        return 0;
    }
}
