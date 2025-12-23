package com.usforever.controller;


import com.usforever.model.Photo;
import com.usforever.service.PhotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@PreAuthorize("permitAll()")
@CrossOrigin
@RequestMapping("/api/photos")
@RestController
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController (PhotoService photoService){
        this.photoService = photoService;
    }

    @GetMapping("")
    public List<Photo> getAllPhotos(){
        return photoService.getAllPhotos();
    }

    @GetMapping("/category/{category}")
    public List<Photo> getAllPhotosFromCategory(@PathVariable String category){
        return photoService.getAllPhotosByCategory(category);
    }

    @GetMapping("/{id}")
    public Photo getPhotoById(@PathVariable int id){
        return photoService.getPhotoById(id);
    }


    @PostMapping("")
    public Photo createPhoto(@Valid @RequestBody Photo newPhoto){
        Photo photo = null;
        photo = photoService.createPhoto(newPhoto);
        return photo;
    }

    @DeleteMapping("/{id}")
    public void deletePhoto(@PathVariable int id){
        int rowsDeleted = photoService.deletePhoto(id);

        if (rowsDeleted == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found");
        }
    }


}
