    package com.example.demo.service;

    import com.example.demo.dto.CreatorRequestTo;
    import com.example.demo.dto.CreatorResponseTo;
    import com.example.demo.exception.DuplicateLoginException;
    import com.example.demo.mapper.CreatorMapper;
    import com.example.demo.model.Creator;
    import com.example.demo.repository.CreatorRepository;
    import jakarta.persistence.EntityNotFoundException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class CreatorService {
        private final CreatorRepository repository;
        private final CreatorMapper mapper;

        public CreatorResponseTo createCreator(CreatorRequestTo creatorRequestTo){
            if (repository.findByLogin(creatorRequestTo.getLogin()).isPresent()) {
                throw new DuplicateLoginException("Login already exists: " + creatorRequestTo.getLogin());
            }

            Creator creator = mapper.toEntity(creatorRequestTo);
            creator = repository.save(creator);
            return mapper.toDto(creator);
        }

        public CreatorResponseTo getCreatorById(Long id){
            Creator creator = repository.findById(id)
                    .orElseThrow(()->new EntityNotFoundException("Creator not found"));
            return mapper.toDto(creator);
        }

        public List<CreatorResponseTo> getAllCreators(){
            return repository.findAll()
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        }

        public void deleteCreator(Long id){
            if(!repository.existsById(id)){
                throw new EntityNotFoundException("Creator not found");
            }
            repository.deleteById(id);
        }

        public CreatorResponseTo updateCreator(CreatorRequestTo request){
            // Валидация входных данных
          //  validateCreatorRequest(request);

            Creator creator = repository.findById(request.getId()).orElseThrow(
                    () -> new EntityNotFoundException("Creator not found"));

            mapper.updateEntityFromDto(request, creator);
            Creator updatedCreator = repository.save(creator);
            return mapper.toDto(updatedCreator);
        }
    }
