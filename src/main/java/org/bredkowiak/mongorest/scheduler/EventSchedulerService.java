package org.bredkowiak.mongorest.scheduler;

import org.bredkowiak.mongorest.beacon.Beacon;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.location.Location;
import org.bredkowiak.mongorest.location.LocationService;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class EventSchedulerService {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(EventSchedulerService.class);
    private final Scheduler scheduler;
    private final LocationService locationService;

    @Autowired
    public EventSchedulerService(Scheduler scheduler, LocationService locationService) {
        this.scheduler = scheduler;
        this.locationService = locationService;
    }

    public Beacon createNewEventCycle(Beacon beacon) throws SchedulerException, NotFoundException {
        String locationId = drawLocation(beacon);
        JobDetail enablerDetail = buildJobDetails(locationId, beacon.getEventDuration());
        Trigger enablerTrigger = buildJobTrigger(enablerDetail, beacon.getEventDuration(), 0);
        scheduler.scheduleJob(enablerDetail, enablerTrigger);

        Logger.info("Created job with name {}", enablerDetail.getKey().getName()); //TODO remove after testing

        beacon.setJobName(enablerDetail.getKey().getName()); //Update beacon with corresponding job
        return beacon;
    }

    public void disableEventCycle(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, "beacon-jobs");
        scheduler.deleteJob(jobKey);
        Logger.info("Successfully deleted job with name: {}", jobName);
    }

    private String drawLocation(Beacon beacon) throws NotFoundException {
        double lat = beacon.getLatitude();
        double lng = beacon.getLongitude();
        Double radiusConverted = 360.0 / 40075 * Double.valueOf(beacon.getRadius()); // approx. kilometers to degree conversion

        //Prepare criteria
        Criteria criteria = new Criteria();
        criteria.where("latitude").lt(lat + radiusConverted).gt(lat - radiusConverted)
                .and("longitude").lt(lng + radiusConverted).gt(lat - radiusConverted); //FIXME add category to criteria

        //Lookup for locations in area
        List<Location> locations = locationService.findLocations(criteria);
        if (locations.isEmpty()) {
            throw new NotFoundException("Couldn't find any locations in beacon area");
        }

        //Draw a random location;
        Random random = new Random();
        int i = random.nextInt(locations.size());
        Location location = locations.get(i);
        return location.getId();

    }

    private JobDetail buildJobDetails(String locationId, int interval) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("locationId", locationId);
        jobDataMap.put("interval", interval);

        return JobBuilder.newJob(EventEnablerJob.class)
                .withIdentity(UUID.randomUUID().toString(), "beacon-jobs")
                .withDescription("Enable Event Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, int interval, int delay) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "beacon-triggers")
                .withDescription("Enable Event Trigger")
                .startAt(DateBuilder.tomorrowAt(0,0,0))
                .withSchedule(CalendarIntervalScheduleBuilder
                                .calendarIntervalSchedule()
                                .withIntervalInDays(interval)
                                .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }

}
