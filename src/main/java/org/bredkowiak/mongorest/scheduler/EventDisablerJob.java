package org.bredkowiak.mongorest.scheduler;

import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.location.Location;
import org.bredkowiak.mongorest.location.LocationService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class EventDisablerJob extends QuartzJobBean {

    private static final Logger Logger = LoggerFactory.getLogger(EventDisablerJob.class);

    @Autowired
    private LocationService locationService;

    @Autowired
    private Scheduler scheduler;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        String locationId = dataMap.getString("locationId");

        //Find location and disable event
        Location location;
        try {
            location = locationService.findOne(locationId);
        } catch (NotFoundException e){
            Logger.error("Error in jobs loop, couldn't find event-triggered location");
            return;
        }
        location.setActiveEvent(false);
        locationService.update(location);

        Logger.info("Disabling event on location with id {}", locationId);
    }








}
