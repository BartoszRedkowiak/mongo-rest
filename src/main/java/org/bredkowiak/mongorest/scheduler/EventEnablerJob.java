package org.bredkowiak.mongorest.scheduler;

import org.bredkowiak.mongorest.beacon.BeaconService;
import org.bredkowiak.mongorest.exception.NotFoundException;
import org.bredkowiak.mongorest.location.Location;
import org.bredkowiak.mongorest.location.LocationService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class EventEnablerJob extends QuartzJobBean {

    private static final Logger Logger = LoggerFactory.getLogger(EventEnablerJob.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private LocationService locationService;

    @Autowired
    private BeaconService beaconService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //Get data from the context
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String locationId = jobDataMap.getString("locationId");
        int interval = jobDataMap.getInt("interval");

        //Set event to active and update entity;
        Location location = null;
        try {
            location = locationService.findOne(locationId);
        } catch (NotFoundException e) {
            //Exception handled already in EventSchedulerService
        }
        location.setActiveEvent(true);
        locationService.update(location);

        Logger.info("Enabling event on location with id {}", location.getId());

        //Schedule disabler job
        JobDetail disablerDetail = buildDisablerDetail(location.getId());
        Trigger disablerTrigger = buildDisablerTrigger(disablerDetail, interval);
        try {
            scheduler.scheduleJob(disablerDetail, disablerTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return;
        }


    }

        private JobDetail buildDisablerDetail(String locationId){
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("locationId", locationId);

            return JobBuilder.newJob(EventDisablerJob.class)
                    .withIdentity(UUID.randomUUID().toString(), "beacon-jobs")
                    .withDescription("Disable Event Job")
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .build();
        }

        private Trigger buildDisablerTrigger(JobDetail jobDetail, int interval){
            Date date = DateBuilder.nextGivenSecondDate(new Date(), interval - 1);

            return TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(jobDetail.getKey().getName(), "beacon-triggers")
                    .withDescription("Enable Event Trigger")
                    .startAt(date)
                    .build();
        }












}
