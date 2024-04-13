package com.example.rv.api.Controllers;

import com.example.rv.impl.creator.Creator;
import com.example.rv.impl.creator.CreatorRequestTo;
import com.example.rv.impl.creator.CreatorResponseTo;
import com.example.rv.impl.creator.CreatorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1.0")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }


    @RequestMapping(value = "/creators", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    List<CreatorResponseTo> getCreators() {
        return creatorService.creatorMapper.creatorToResponseTo(creatorService.creatorCrudRepository.getAll());
    }

    @RequestMapping(value = "/creators", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    CreatorResponseTo makeCreator(@RequestBody CreatorRequestTo creatorRequestTo) {

        var toBack = creatorService.creatorCrudRepository.save(
                creatorService.creatorMapper.dtoToEntity(creatorRequestTo)
        );

        Creator creator = toBack.orElse(null);

        assert creator != null;
        return creatorService.creatorMapper.creatorToResponseTo(creator);
    }

    @RequestMapping(value = "/creators/{id}", method = RequestMethod.GET)
    CreatorResponseTo getCreator(@PathVariable Long id) {
        return creatorService.creatorMapper.creatorToResponseTo(
                Objects.requireNonNull(creatorService.creatorCrudRepository.getById(id).orElse(null)));
    }

    @RequestMapping(value = "/creators", method = RequestMethod.PUT)
    CreatorResponseTo updateCreator(@RequestBody CreatorRequestTo creatorRequestTo, HttpServletResponse response) {
        Creator creator = creatorService.creatorMapper.dtoToEntity(creatorRequestTo);
        var newcreator = creatorService.creatorCrudRepository.update(creator).orElse(null);
        if (newcreator != null) {
            response.setStatus(200);
            return creatorService.creatorMapper.creatorToResponseTo(newcreator);
        } else{
            response.setStatus(403);
            return creatorService.creatorMapper.creatorToResponseTo(creator);
        }
    }

    @RequestMapping(value = "/creators/{id}", method = RequestMethod.DELETE)
    int deleteCreator(@PathVariable Long id, HttpServletResponse response) {
        Creator edToDelete = creatorService.creatorCrudRepository.getById(id).orElse(null);
        if (Objects.isNull(edToDelete)) {
            response.setStatus(403);
        } else {
            creatorService.creatorCrudRepository.delete(edToDelete);
            response.setStatus(204);
        }
        return 0;
    }
}
