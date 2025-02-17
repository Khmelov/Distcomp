package by.kapinskiy.Task310.services;


import by.kapinskiy.Task310.models.Issue;
import by.kapinskiy.Task310.models.Note;
import by.kapinskiy.Task310.models.User;
import by.kapinskiy.Task310.repositories.IssuesRepository;
import by.kapinskiy.Task310.repositories.NotesRepository;
import by.kapinskiy.Task310.repositories.UsersRepository;
import by.kapinskiy.Task310.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotesService {
    private final NotesRepository notesRepository;
    private final IssuesRepository issuesRepository;

    @Autowired
    public NotesService(NotesRepository notesRepository, IssuesRepository issuesRepository) {
        this.notesRepository = notesRepository;
        this.issuesRepository = issuesRepository;
    }

    private void setIssue(Note note, long issueId){
        Issue issue = issuesRepository.findById(issueId).orElseThrow(() -> new NotFoundException("Issue with such id does not exist"));
        note.setIssue(issue);
    }
    public Note save(Note note, long issueId) {
        setIssue(note, issueId);
        return notesRepository.save(note);
    }

    public List<Note> findAll() {
        return notesRepository.findAll();
    }

    public Note findById(long id) {
        return notesRepository.findById(id).orElseThrow(() -> new NotFoundException("Note with such id does not exist"));
    }

    public void deleteById(long id) {
        if (!notesRepository.existsById(id)) {
            throw new NotFoundException("Note not found");
        }
        notesRepository.deleteById(id);
    }

    public Note update(Note note, Long issueId) {
        if (issueId != null) {
            setIssue(note, issueId);
        }
        return notesRepository.save(note);
    }

}
