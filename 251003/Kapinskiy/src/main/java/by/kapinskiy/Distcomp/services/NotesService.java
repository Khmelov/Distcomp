package by.kapinskiy.Distcomp.services;


import by.kapinskiy.Distcomp.DTOs.Requests.NoteRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.NoteResponseDTO;
import by.kapinskiy.Distcomp.models.Issue;
import by.kapinskiy.Distcomp.models.Note;
import by.kapinskiy.Distcomp.repositories.IssuesRepository;
import by.kapinskiy.Distcomp.repositories.NotesRepository;
import by.kapinskiy.Distcomp.utils.exceptions.NotFoundException;
import by.kapinskiy.Distcomp.utils.mappers.NotesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesService {
    private final NotesRepository notesRepository;
    private final IssuesRepository issuesRepository;
    private final NotesMapper notesMapper;

    @Autowired
    public NotesService(NotesRepository notesRepository, IssuesRepository issuesRepository, NotesMapper notesMapper) {
        this.notesRepository = notesRepository;
        this.issuesRepository = issuesRepository;
        this.notesMapper = notesMapper;
    }

    private void setIssue(Note note, long issueId){
        Issue issue = issuesRepository.findById(issueId).orElseThrow(() -> new NotFoundException("Issue with such id does not exist"));
        note.setIssue(issue);
    }
    public NoteResponseDTO save(NoteRequestDTO noteRequestDTO) {
        Note note = notesMapper.toNote(noteRequestDTO);
        setIssue(note, noteRequestDTO.getIssueId());
        return notesMapper.toNoteResponse(notesRepository.save(note));
    }

    public List<NoteResponseDTO> findAll() {
        return notesMapper.toNoteResponseList(notesRepository.findAll());
    }

    public NoteResponseDTO findById(long id) {
        return notesMapper.toNoteResponse(notesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note with such id does not exist")));
    }

    public void deleteById(long id) {
        if (!notesRepository.existsById(id)) {
            throw new NotFoundException("Note not found");
        }
        notesRepository.deleteById(id);
    }

    public NoteResponseDTO update(NoteRequestDTO noteRequestDTO) {
        Note note = notesMapper.toNote(noteRequestDTO);
        Long issueId = noteRequestDTO.getIssueId();
        if (issueId != null) {
            setIssue(note, issueId);
        }
        return notesMapper.toNoteResponse(notesRepository.save(note));
    }

}
