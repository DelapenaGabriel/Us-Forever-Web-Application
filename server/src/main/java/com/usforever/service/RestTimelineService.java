package com.usforever.service;


import com.usforever.dao.TimelineDao;
import com.usforever.model.Timeline;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestTimelineService implements TimelineService {

    private final TimelineDao timelineDao;

    public RestTimelineService (TimelineDao timelineDao){
        this.timelineDao = timelineDao;
    }

    @Override
    public List<Timeline> getAllTimeline() {
        return timelineDao.getAllTimeline();
    }

    @Override
    public Timeline getTimelineById(int id) {
        return timelineDao.getTimelineById(id);
    }

    @Override
    public Timeline createTimeline(Timeline newTimeline) {
        return timelineDao.createTimeline(newTimeline);
    }

    @Override
    public Timeline updateTimeline(Timeline updatedTimeline) {
        return timelineDao.updateTimeline(updatedTimeline);
    }

    @Override
    public int deleteTimeline(int id) {
        return timelineDao.deleteTimeline(id);
    }
}
