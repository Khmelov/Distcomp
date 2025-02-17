package by.kapinskiy.Task310.utils;

import by.kapinskiy.Task310.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Task310.services.IssuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class IssueValidator implements Validator {
    private final IssuesService issuesService;

    @Autowired
    public IssueValidator(IssuesService issuesService) {
        this.issuesService = issuesService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return IssueRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasFieldErrors()){
            IssueRequestDTO issue = (IssueRequestDTO) target;
            if (issuesService.existsByTitle(issue.getTitle())){
                errors.rejectValue("title", null, "Issue with such title already exists");
            }
        }
    }
}
