package by.kapinskiy.Distcomp.controllers;


import by.kapinskiy.Distcomp.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.IssueResponseDTO;
import by.kapinskiy.Distcomp.models.Issue;
import by.kapinskiy.Distcomp.services.IssuesService;
import by.kapinskiy.Distcomp.utils.IssueValidator;
import by.kapinskiy.Distcomp.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issues")
public class IssuesController {
    private final IssuesService issuesService;
    private final IssueValidator issueValidator;

    @Autowired
    public IssuesController(IssuesService issuesService, IssueValidator issueValidator) {
        this.issuesService = issuesService;
        this.issueValidator = issueValidator;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueResponseDTO createIssue(@RequestBody @Valid IssueRequestDTO issueRequestDTO, BindingResult bindingResult) {
        validate(issueRequestDTO, bindingResult);
        return issuesService.save(issueRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IssueResponseDTO> getAllIssues() {
        return issuesService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDTO getByIssueById(@PathVariable Long id) {
        return issuesService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIssue(@PathVariable long id){
        issuesService.deleteById(id);
    }


    // Non REST version for tests compliance
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDTO updateIssue(@RequestBody @Valid IssueRequestDTO issueRequestDTO, BindingResult bindingResult){
        validate(issueRequestDTO, bindingResult);
        return issuesService.update(issueRequestDTO);
    }

    private void validate(IssueRequestDTO issueRequestDTO, BindingResult bindingResult){
        issueValidator.validate(issueRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
