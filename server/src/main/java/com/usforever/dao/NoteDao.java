package com.usforever.dao;

import com.usforever.model.Note;

import java.util.List;

public interface NoteDao {
    List<Note> getAllNotes();

    Note getNoteById(int id);

    Note createNote(Note newNote);

    Note updateNote(Note updatedNote);

    int deleteNote(int id);
}
