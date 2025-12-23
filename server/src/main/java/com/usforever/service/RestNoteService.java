package com.usforever.service;

import com.usforever.dao.NoteDao;
import com.usforever.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestNoteService implements NoteService {

    private final NoteDao noteDao;

    public RestNoteService (NoteDao noteDao){

        this.noteDao = noteDao;
    }

    @Override
    public List<Note> getAllNotes() {
        return noteDao.getAllNotes();
    }

    @Override
    public Note getNoteById(int id) {
        return noteDao.getNoteById(id);
    }

    @Override
    public Note createNote(Note newNote) {
        return noteDao.createNote(newNote);
    }

    @Override
    public Note updateNote(Note updatedNote) {
        return noteDao.updateNote(updatedNote);
    }

    @Override
    public int deleteNote(int id) {
        return noteDao.deleteNote(id);
    }
}
