package com.usforever.dao;

import com.usforever.model.Timeline;

import java.sql.Time;
import java.util.List;

public interface TimelineDao {
    List<Timeline> getAllTimeline();

    Timeline getTimelineById(int id);

    Timeline createTimeline (Timeline newTimeline);

    Timeline updateTimeline (Timeline updatedTimeline);

    int deleteTimeline(int id);
}
