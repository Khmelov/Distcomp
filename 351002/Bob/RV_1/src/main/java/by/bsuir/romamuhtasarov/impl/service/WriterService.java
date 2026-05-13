package by.bsuir.romamuhtasarov.impl.service;

import by.bsuir.romamuhtasarov.api.Service;
import by.bsuir.romamuhtasarov.impl.bean.Writer;
import by.bsuir.romamuhtasarov.impl.dto.WriterRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.WriterResponseTo;
import by.bsuir.romamuhtasarov.api.WriterMapper;
import by.bsuir.romamuhtasarov.impl.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WriterService implements Service<WriterResponseTo, WriterRequestTo> {

    @Autowired
    private WriterRepository writerRepository;

    public WriterService() {

    }

    public List<WriterResponseTo> getAll() {
        List<Writer> writerList = writerRepository.getAll();
        List<WriterResponseTo> resultList = new ArrayList<>();
        for (int i = 0; i < writerList.size(); i++) {
            resultList.add(WriterMapper.INSTANCE.WriterToWriterResponseTo(writerList.get(i)));
        }
        return resultList;
    }

    public WriterResponseTo update(WriterRequestTo updatingWriter) {
        Writer writer = WriterMapper.INSTANCE.WriterRequestToToWriter(updatingWriter);
        if (validateWriter(writer)) {
            boolean result = writerRepository.update(writer);
            WriterResponseTo responseTo = result ? WriterMapper.INSTANCE.WriterToWriterResponseTo(writer) : null;
            return responseTo;
        } else return new WriterResponseTo();
        //return responseTo;
    }

    public WriterResponseTo get(long id) {
        return WriterMapper.INSTANCE.WriterToWriterResponseTo(writerRepository.get(id));
    }

    public WriterResponseTo delete(long id) {
        return WriterMapper.INSTANCE.WriterToWriterResponseTo(writerRepository.delete(id));
    }

    public WriterResponseTo add(WriterRequestTo writerRequestTo) {
        Writer writer = WriterMapper.INSTANCE.WriterRequestToToWriter(writerRequestTo);
        return WriterMapper.INSTANCE.WriterToWriterResponseTo(writerRepository.insert(writer));
    }

    private boolean validateWriter(Writer writer) {
        String firstname = writer.getFirstname();
        String lastname = writer.getLastname();
        String login = writer.getLogin();
        String password = writer.getPassword();
        return (firstname.length() >= 2 && firstname.length() <= 64) && (lastname.length() >= 2 && lastname.length() <= 64) && (password.length() >= 8 && firstname.length() <= 128) && (login.length() >= 2 && login.length() <= 64);
    }
}