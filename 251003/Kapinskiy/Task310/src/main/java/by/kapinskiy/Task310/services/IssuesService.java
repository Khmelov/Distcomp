package by.kapinskiy.Task310.services;


import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.models.User;
import by.kapinskiy.Task310.repositories.IssuesRepository;
import by.kapinskiy.Task310.repositories.UsersRepository;
import by.kapinskiy.Task310.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IssuesService {
    private final IssuesRepository issuesRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public IssuesService(IssuesRepository issuesRepository, UsersRepository usersRepository) {
        this.issuesRepository = issuesRepository;
        this.usersRepository = usersRepository;
    }

    private void setUser(Issue issue, long userId){
        User user = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with such id does not exist"));
        issue.setUser(user);
    }
    public Issue save(Issue issue, long userId) {
        setUser(issue, userId);
        issue.setCreated(new Date());
        issue.setModified(new Date());
        return issuesRepository.save(issue);
    }

    public List<Issue> findAll() {
        return issuesRepository.findAll();
    }

    public Issue findById(long id) {
        return issuesRepository.findById(id).orElseThrow(() -> new NotFoundException("Issue with such id does not exist"));
    }

    public void deleteById(long id) {

        if (!issuesRepository.existsById(id)) {
            throw new NotFoundException("Issue not found");
        }
        issuesRepository.deleteById(id);
    }

    public Issue update(Issue issue, Long userId) {
        if (userId != null) {
            setUser(issue, userId);
        }
        issue.setModified(new Date());
        return issuesRepository.save(issue);
    }

    public boolean existsByTitle(String title){
        return issuesRepository.existsByTitle(title);
    }
}
