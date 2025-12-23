package com.usforever.dao;

import com.usforever.exception.DaoException;
import com.usforever.model.Note;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcNoteDao implements NoteDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcNoteDao (DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private Note mapRowToNote(SqlRowSet rs){
        Note note = new Note();
        note.setId(rs.getInt("id"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        if (rs.getTimestamp("created_at") != null){
            note.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return note;
    }

    @Override
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        String sql = "SELECT * FROM notes ORDER BY created_at DESC;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while(result.next()){
                notes.add(mapRowToNote(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return notes;
    }

    @Override
    public Note getNoteById(int id) {
        Note note = null;

        String sql = "SELECT * FROM notes WHERE id = ?;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()){
                note = mapRowToNote(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return note;
    }

    @Override
    public Note createNote(Note newNote) {
        Note note = null;
        int newId;

        String sql = "INSERT INTO notes (title, content) " +
                "VALUES (?, ?) RETURNING id;";

        try{
            newId = jdbcTemplate.queryForObject(sql, int.class, newNote.getTitle(), newNote.getContent());
            note = getNoteById(newId);
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return note;
    }

    @Override
    public Note updateNote(Note updatedNote) {
        Note note = null;

        String sql = "UPDATE notes " +
                "SET title = ?, content = ? " +
                "WHERE id = ?;";

        try{
            int rowsAffected = jdbcTemplate.update(sql, updatedNote.getTitle(), updatedNote.getContent(), updatedNote.getId());
            if (rowsAffected == 0){
                throw new DaoException("Zero rows affected, expected at least one");
            }
            note = getNoteById(updatedNote.getId());
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return note;
    }

    @Override
    public int deleteNote(int id) {
        String sql = "DELETE FROM notes WHERE id = ?;";

        try{
            return jdbcTemplate.update(sql, id);
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
    }
}
