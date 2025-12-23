package com.usforever.service;

import com.usforever.model.Timeline;

import java.util.List;

public interface TimelineService {
    List<Timeline> getAllTimeline();

    Timeline getTimelineById(int id);

    Timeline createTimeline (Timeline newTimeline);

    Timeline updateTimeline (Timeline updatedTimeline);

    int deleteTimeline(int id);
}
