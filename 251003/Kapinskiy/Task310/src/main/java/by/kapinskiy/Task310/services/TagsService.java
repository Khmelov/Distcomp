package by.kapinskiy.Task310.services;


import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.models.Tag;
import by.kapinskiy.Task310.repositories.IssuesRepository;
import by.kapinskiy.Task310.repositories.TagsRepository;
import by.kapinskiy.Task310.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsService {
    private final TagsRepository tagsRepository;
    private final IssuesRepository issuesRepository;
    

    @Autowired
    public TagsService(TagsRepository tagsRepository, IssuesRepository issuesRepository) {
        this.tagsRepository = tagsRepository;
        this.issuesRepository = issuesRepository;
    }

    public Tag save(Tag tag, long issueId) {
        Issue issue = issuesRepository.findById(issueId).orElseThrow(() -> new NotFoundException("Issue with such id does not exist"));
        issue.getTags().add(tag);
        tag.getIssues().add(issue);
        return tagsRepository.save(tag);
    }

    public Tag save(Tag tag) {
        return tagsRepository.save(tag);
    }
    public List<Tag> findAll() {
        return tagsRepository.findAll();
    }

    public Tag findById(long id) {
        return tagsRepository.findById(id).orElseThrow(() -> new NotFoundException("Tag with such id does not exist"));
    }

    public void deleteById(long id) {
        if (!tagsRepository.existsById(id))
            throw new NotFoundException("Tag with such id not found");
        tagsRepository.deleteById(id);
    }

    public Tag update(Tag tag) {
        return tagsRepository.save(tag);
    }

    public boolean existsByName(String name){
        return tagsRepository.existsByName(name);
    }

}
