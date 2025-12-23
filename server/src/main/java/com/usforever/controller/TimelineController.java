package com.usforever.controller;


import com.usforever.model.Timeline;
import com.usforever.service.TimelineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@PreAuthorize("permitAll()")
@CrossOrigin
@RequestMapping("/api/timeline")
@RestController
public class TimelineController {

    private final TimelineService timelineService;

    public TimelineController(TimelineService timelineService){
        this.timelineService = timelineService;
    }

    @GetMapping("")
    public List<Timeline> getAllTimeline(){
        return timelineService.getAllTimeline();
    }

    @GetMapping("/{id}")
    public Timeline getTimelineById(@PathVariable int id){
        return timelineService.getTimelineById(id);
    }

    @PostMapping("")
    public Timeline createTimeline(@Valid @RequestBody Timeline newTimeline){
        Timeline timeline = null;
        timeline = timelineService.createTimeline(newTimeline);
        return timeline;
    }

    @PutMapping("/{id}")
    public Timeline updateTimeline(@PathVariable int id, @Valid @RequestBody Timeline updatedTimeline){
        updatedTimeline.setId(id);

        Timeline toUpdate = timelineService.updateTimeline(updatedTimeline);

        return toUpdate;
    }

    @DeleteMapping("/{id}")
    public void deleteTimeline(@PathVariable int id){
        int rowsDeleted = timelineService.deleteTimeline(id);
        if (rowsDeleted == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
    }


}
