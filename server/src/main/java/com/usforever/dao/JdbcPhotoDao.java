package com.usforever.dao;

import com.usforever.exception.DaoException;
import com.usforever.model.Photo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPhotoDao implements PhotoDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcPhotoDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    @Override
    public List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<>();

        String sql = "SELECT * FROM photos ORDER BY RANDOM();";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while(result.next()){
                photos.add(mapRowToPhoto(result));
            }
        }catch (CannotGetJdbcConnectionException e ){
            throw new DaoException("Cannot connect to database", e);
        }
        return photos;
    }

    @Override
    public List<Photo> getAllPhotosByCategory(String category) {
        List<Photo> photos = new ArrayList<>();

        String sql = "SELECT * FROM photos WHERE category ILIKE ? ORDER BY RANDOM();";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, category);
            while(result.next()){
                photos.add(mapRowToPhoto(result));
            }
        }catch (CannotGetJdbcConnectionException e ){
            throw new DaoException("Cannot connect to database", e);
        }
        return photos;
    }

    @Override
    public Photo getPhotoById(int id) {
        Photo photo = null;

        String sql = "SELECT * FROM photos WHERE id = ?;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()){
                photo = mapRowToPhoto(result);
            }
        }catch (CannotGetJdbcConnectionException e ){
            throw new DaoException("Cannot connect to database", e);
        }
        return photo;
    }

    @Override
    public Photo createPhoto(Photo newPhoto) {
        Photo photo = null;
        int newId;

        String sql = "INSERT INTO photos (category, img_url) " +
                "VALUES (?, ?) RETURNING id;";

        try{
            newId = jdbcTemplate.queryForObject(sql, int.class, newPhoto.getCategory(), newPhoto.getImgUrl());
            photo = getPhotoById(newId);
        }catch (CannotGetJdbcConnectionException e ){
            throw new DaoException("Cannot connect to database", e);
        }catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
        return photo;
    }

    @Override
    public int deletePhoto(int id) {
        String sql = "DELETE FROM photos WHERE id = ?;";

        try{
            return jdbcTemplate.update(sql, id);
        }catch (CannotGetJdbcConnectionException e ){
            throw new DaoException("Cannot connect to database", e);
        }catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Photo mapRowToPhoto(SqlRowSet rowSet){
        Photo photo = new Photo();
        photo.setId(rowSet.getInt("id"));
        photo.setCategory(rowSet.getString("category"));
        photo.setImgUrl(rowSet.getString("img_url"));
        return photo;
    }
}
