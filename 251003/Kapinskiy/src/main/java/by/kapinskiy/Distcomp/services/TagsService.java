package by.kapinskiy.Distcomp.services;


import by.kapinskiy.Distcomp.DTOs.Requests.TagRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.TagResponseDTO;
import by.kapinskiy.Distcomp.models.Issue;
import by.kapinskiy.Distcomp.models.Tag;
import by.kapinskiy.Distcomp.repositories.IssuesRepository;
import by.kapinskiy.Distcomp.repositories.TagsRepository;
import by.kapinskiy.Distcomp.utils.exceptions.NotFoundException;
import by.kapinskiy.Distcomp.utils.mappers.TagsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsService {
    private final TagsRepository tagsRepository;
    private final IssuesRepository issuesRepository;
    private final TagsMapper tagsMapper;
    

    @Autowired
    public TagsService(TagsRepository tagsRepository, IssuesRepository issuesRepository, TagsMapper tagsMapper) {
        this.tagsRepository = tagsRepository;
        this.issuesRepository = issuesRepository;
        this.tagsMapper = tagsMapper;
    }

    public TagResponseDTO save(Tag tag, long issueId) {
        Issue issue = issuesRepository.findById(issueId).orElseThrow(() -> new NotFoundException("Issue with such id does not exist"));
        issue.getTags().add(tag);
        tag.getIssues().add(issue);
        return tagsMapper.toTagResponse(tagsRepository.save(tag));
    }

    public TagResponseDTO save(TagRequestDTO tagRequestDTO) {
        Tag tag = tagsMapper.toTag(tagRequestDTO);
        return tagsMapper.toTagResponse(tagsRepository.save(tag));
    }
    public List<TagResponseDTO> findAll() {
        return tagsMapper.toTagResponseList(tagsRepository.findAll());
    }

    public TagResponseDTO findById(long id) {
        return tagsMapper.toTagResponse(tagsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag with such id does not exist")));
    }

    public void deleteById(long id) {
        if (!tagsRepository.existsById(id))
            throw new NotFoundException("Tag with such id not found");
        tagsRepository.deleteById(id);
    }

    public TagResponseDTO update(TagRequestDTO tagRequestDTO) {
        Tag tag = tagsMapper.toTag(tagRequestDTO);
        return tagsMapper.toTagResponse(tagsRepository.save(tag));
    }

    public boolean existsByName(String name){
        return tagsRepository.existsByName(name);
    }

}
