package com.usforever.service;

import com.usforever.dao.PhotoDao;
import com.usforever.model.Photo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestPhotoService implements PhotoService{

    private final PhotoDao photoDao;

    public RestPhotoService(PhotoDao photoDao){
        this.photoDao = photoDao;
    }


    @Override
    public List<Photo> getAllPhotos() {
        return photoDao.getAllPhotos();
    }

    @Override
    public List<Photo> getAllPhotosByCategory(String category) {
        return photoDao.getAllPhotosByCategory(category);
    }

    @Override
    public Photo getPhotoById(int id) {
        return photoDao.getPhotoById(id);
    }

    @Override
    public Photo createPhoto(Photo newPhoto) {
        return photoDao.createPhoto(newPhoto);
    }

    @Override
    public int deletePhoto(int id) {
        return photoDao.deletePhoto(id);
    }
}
