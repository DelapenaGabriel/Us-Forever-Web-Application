package com.usforever.service;

import com.usforever.model.Photo;

import java.util.List;

public interface PhotoService {
    List<Photo> getAllPhotos();
    List<Photo> getAllPhotosByCategory(String category);
    Photo getPhotoById(int id);
    Photo createPhoto(Photo newPhoto);
    int deletePhoto(int id);
}
