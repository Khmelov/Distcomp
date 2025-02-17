package by.kapinskiy.Task310.controllers;


import by.kapinskiy.Task310.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Task310.DTOs.Responses.IssueResponseDTO;
import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.services.IssuesService;
import by.kapinskiy.Task310.utils.IssueValidator;
import by.kapinskiy.Task310.utils.Mapper;
import by.kapinskiy.Task310.utils.exceptions.ValidationException;
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
    private final Mapper mapper;
    private final IssuesService issuesService;
    private final IssueValidator issueValidator;

    @Autowired
    public IssuesController(Mapper mapper, IssuesService issuesService, IssueValidator issueValidator) {
        this.mapper = mapper;
        this.issuesService = issuesService;
        this.issueValidator = issueValidator;
    }


    @PostMapping
    public ResponseEntity<IssueResponseDTO> createIssue(@RequestBody @Valid IssueRequestDTO issueRequestDTO, BindingResult bindingResult) {
        validate(issueRequestDTO, bindingResult);
        Issue issue = mapper.issueRequestToIssue(issueRequestDTO);
        return new ResponseEntity<>(mapper.issueToIssueResponse(issuesService.save(issue, issueRequestDTO.getUserId())), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDTO>> getAllIssues() {
        return new ResponseEntity<>(mapper.issuesToIssueResponses(issuesService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> getByIssueById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.issueToIssueResponse(issuesService.findById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable long id){
        issuesService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // The same as in Users Put
    @PutMapping
    public ResponseEntity<IssueResponseDTO> updateIssue(@RequestBody @Valid IssueRequestDTO issueRequestDTO, BindingResult bindingResult){
        validate(issueRequestDTO, bindingResult);
        Issue updatedIssue = issuesService.update(mapper.issueRequestToIssue(issueRequestDTO), issueRequestDTO.getUserId());
        return new ResponseEntity<>(mapper.issueToIssueResponse(updatedIssue), HttpStatus.OK);
    }

    private void validate(IssueRequestDTO issueRequestDTO, BindingResult bindingResult){
        issueValidator.validate(issueRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()){
            throw new ValidationException(bindingResult);
        }
    }
}
