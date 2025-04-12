package com.lab1.lab1DC.controller;

import com.lab1.lab1DC.dto.*;
import com.lab1.lab1DC.entity.Sticker;
import com.lab1.lab1DC.entity.Story;
import com.lab1.lab1DC.entity.StorySticker;
import com.lab1.lab1DC.repository.StoryStickerRepositoryJPA;
import com.lab1.lab1DC.service.StickerService;
import com.lab1.lab1DC.service.StoryService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1.0/stories")
public class StoryController {

    private final StoryService storyService;
    private final StickerService stickerService;
    private final StoryStickerRepositoryJPA storyStickerRepositoryJPA;

    public StoryController(StoryService storyService, StickerService stickerService, StoryStickerRepositoryJPA stickerRepositoryJPA) {
        this.storyService = storyService;
        this.stickerService = stickerService;
        this.storyStickerRepositoryJPA = stickerRepositoryJPA;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StoryResponseTo> createStory(@RequestBody StoryRequestTo storyRequestTo) {
        try {
            if(!validateStory(storyRequestTo)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new StoryResponseTo());
            }
            Long id = System.currentTimeMillis();
            Story story = new Story(id, storyRequestTo.getTitle(), storyRequestTo.getContent(), storyRequestTo.getUserId(), storyRequestTo.getStickerIds(), new ArrayList<>());
            List <String> stickers = storyRequestTo.getStickers();
            StoryResponseTo response = storyService.createStory(story);
            if(stickers != null)
            for (String st : stickers){
                Long idSticker = System.currentTimeMillis();
                StorySticker svSticker;
                try {
                    Sticker newSt = new Sticker(idSticker, st);
                    stickerService.createSticker(newSt);

                    //Правильное удаление
                    storyStickerRepositoryJPA.save(new StorySticker(System.currentTimeMillis(), story.getId(), newSt.getId()));

                } catch (Exception e){
                    System.out.println(e.toString());
                }

            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new StoryResponseTo());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StoryResponseTo());
        }
    }

    @GetMapping
    public ResponseEntity<List<StoryResponseTo>> getAllStory() {
        try {
            List<StoryResponseTo> response = storyService.getAllStory();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseTo> getStoryById(@PathVariable Long id) {
        try {
            StoryResponseTo response = storyService.getStoryById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StoryResponseTo());
        }
    }

    @PutMapping
    public ResponseEntity<StoryResponseTo> updateStory(@RequestBody StoryRequestTo storyRequestTo) {
        try {
            if (storyRequestTo.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StoryResponseTo());
            }
            StoryResponseTo updatedStory = storyService.updateStory(storyRequestTo.getId(), storyRequestTo);
            return ResponseEntity.ok(updatedStory);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StoryResponseTo());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StoryResponseTo());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StoryResponseTo());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        try {
            storyService.deleteStory(id);
            /*
            Правильное удаление
            List <Long> idStickersToDelete = storyStickerRepositoryJPA.findStickerIdsByStoryId(id);
            if(idStickersToDelete != null) {
                for (Long ind : idStickersToDelete) {
                    stickerService.deleteSticker(ind);
                }
                storyStickerRepositoryJPA.deleteByStoryId(id);
            }

             */
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private boolean validateStory(StoryRequestTo storyRequestTo) {

        if (storyRequestTo.getTitle().length() < 2 ||
                storyRequestTo.getTitle().length() > 64) {
            return false;
        }
        if (storyRequestTo.getContent().length() < 4 ||
                storyRequestTo.getContent().length() > 2048) {
            return false;
        }
        return true;
    }

}

