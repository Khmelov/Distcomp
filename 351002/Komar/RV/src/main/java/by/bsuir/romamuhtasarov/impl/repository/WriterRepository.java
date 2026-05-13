package by.bsuir.romamuhtasarov.impl.repository;

import by.bsuir.romamuhtasarov.api.InMemoryRepository;
import by.bsuir.romamuhtasarov.impl.bean.Writer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WriterRepository implements InMemoryRepository<Writer> {

    private final Map<Long, Writer> WriterMemory = new HashMap<>();

    @Override
    public Writer get(long id) {
        Writer writer = WriterMemory.get(id);
        if (writer != null) {
            writer.setId(id);
        }
        return writer;
    }

    @Override
    public List<Writer> getAll() {
        List<Writer> writerList = new ArrayList<>();
        for (Long key : WriterMemory.keySet()) {
            Writer writer = WriterMemory.get(key);
            writer.setId(key);
            writerList.add(writer);
        }
        return writerList;
    }

    @Override
    public Writer delete(long id) {
        return WriterMemory.remove(id);
    }

    @Override
    public Writer insert(Writer insertObject) {
        WriterMemory.put(insertObject.getId(), insertObject);
        return insertObject;
    }

    @Override
    public boolean update(Writer updatingValue) {
        return WriterMemory.replace(updatingValue.getId(), WriterMemory.get(updatingValue.getId()), updatingValue);
    }

}