package com.usforever.controller;

import com.usforever.model.Note;
import com.usforever.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@PreAuthorize("permitAll()")
@CrossOrigin
@RequestMapping("/api/notes")
@RestController
public class NoteController {
    private final NoteService noteService;

    public NoteController (NoteService noteService){
        this.noteService = noteService;
    }

    @GetMapping("")
    public List<Note> getAllNotes(){
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable int id){
        return noteService.getNoteById(id);
    }

    @PostMapping("")
    public Note createNote(@Valid @RequestBody Note newNote){
        Note note = null;
        note = noteService.createNote(newNote);
        return note;
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable int id, @Valid @RequestBody Note updatedNote){
        updatedNote.setId(id);

        Note toUpdate = noteService.updateNote(updatedNote);

        return toUpdate;
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable int id){
        int rowsDeleted = noteService.deleteNote(id);
        if (rowsDeleted == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
    }

}
