package by.kapinskiy.Task310.utils;

import by.kapinskiy.Task310.DTOs.Requests.IssueRequestDTO;
import by.kapinskiy.Task310.DTOs.Requests.NoteRequestDTO;
import by.kapinskiy.Task310.DTOs.Requests.TagRequestDTO;
import by.kapinskiy.Task310.DTOs.Requests.UserRequestDTO;
import by.kapinskiy.Task310.DTOs.Responses.IssueResponseDTO;
import by.kapinskiy.Task310.DTOs.Responses.NoteResponseDTO;
import by.kapinskiy.Task310.DTOs.Responses.TagResponseDTO;
import by.kapinskiy.Task310.DTOs.Responses.UserResponseDTO;
import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.models.Note;
import by.kapinskiy.Task310.models.Tag;
import by.kapinskiy.Task310.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {
    private final ModelMapper modelMapper;

    @Autowired
    public Mapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User userRequestToUser(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, User.class);
    }

    public UserResponseDTO userToUserResponse(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public List<UserResponseDTO> usersToUserResponses(List<User> users) {
        return users.stream().map(this::userToUserResponse).collect(Collectors.toList());
    }

    public Issue issueRequestToIssue(IssueRequestDTO issueRequestDTO) {
        return modelMapper.map(issueRequestDTO, Issue.class);
    }

    public IssueResponseDTO issueToIssueResponse(Issue issue) {
        IssueResponseDTO dto = modelMapper.map(issue, IssueResponseDTO.class);
        dto.setUserId(issue.getUser().getId());
        return dto;
    }

    public List<IssueResponseDTO> issuesToIssueResponses(List<Issue> issues) {
        return issues.stream().map(this::issueToIssueResponse).collect(Collectors.toList());
    }

    public Note noteRequestToNote(NoteRequestDTO noteRequestDTO){
        return modelMapper.map(noteRequestDTO, Note.class);
    }

    public NoteResponseDTO noteToNoteResponse(Note note){
        NoteResponseDTO dto = modelMapper.map(note, NoteResponseDTO.class);
        dto.setIssueId(note.getIssue().getId());
        return dto;
    }

    public List<NoteResponseDTO> notesToNoteResponses(List<Note> notes){
        return notes.stream().map(this::noteToNoteResponse).collect(Collectors.toList());
    }

    public Tag tagRequestToTag(TagRequestDTO tagRequestDTO){
        return modelMapper.map(tagRequestDTO, Tag.class);
    }

    public TagResponseDTO tagToTagResponse(Tag tag){
        return modelMapper.map(tag, TagResponseDTO.class);
    }

    public List<TagResponseDTO> tagsToTagResponses(List<Tag> tags){
        return tags.stream().map(this::tagToTagResponse).collect(Collectors.toList());
    }
}
