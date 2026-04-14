package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.service.dto.ScheduleDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuartzWorkerService {

    public static String cleanVCDataTaskNamePrefix = "CleanVCItemData_";

    @Getter
    private Map<Long, ScheduleDTO> runScheduleMap = new ConcurrentHashMap<>();

    public String generateCronString(ScheduleDTO scheduleDTO) {
        //轉換week
        Map<String, String> transferMap = new HashMap<>();
        transferMap.put("1", "2");
        transferMap.put("2", "3");
        transferMap.put("3", "4");
        transferMap.put("4", "5");
        transferMap.put("5", "6");
        transferMap.put("6", "7");
        transferMap.put("7", "1");
        String timePrefix = "0 $minute $hour ";
        String DailyPattern = "* * ?";
        String WeeklyPattern = "? * $week";
        String MonthlyPattern = "$date * ?";
        String QuarterlyPattern = "$date $month ?";
        String[] timeString = scheduleDTO.getTime().split(":");
        String time = timePrefix.replace("$minute", timeString[1]).replace("$hour", timeString[0]);
        switch (scheduleDTO.getType()) {
            case "Daily":
                return time + DailyPattern;
            case "Weekly":
                return time + WeeklyPattern.replace("$week", transferMap.get(scheduleDTO.getWeek()));
            case "Monthly":
                return time + MonthlyPattern.replace("$date", scheduleDTO.getDate());
            case "Quarterly":
                return time + QuarterlyPattern.replace("$date", scheduleDTO.getDate()).replace("$month", scheduleDTO.getMonth());
            default:
                return null;
        }
    }

    private final Scheduler scheduler;

    public static String DefaultTimeZone = "Etc/GMT-8"; // +8時區

    public QuartzWorkerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 新增任務--cron
     * @param jobClass jobClass
     * @param taskName jobName、triggerName使用同一個name
     * @param cron cron定時任務規則
     */
    public void addCronJob(Class<? extends Job> jobClass, String taskName, String cron, String oriTimezone) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(taskName).build();
        String timezone = StringUtils.hasLength(oriTimezone) ? oriTimezone : DefaultTimeZone;
        CronScheduleBuilder cronScheduler = CronScheduleBuilder.cronSchedule(cron)
            .inTimeZone(TimeZone.getTimeZone(timezone))
            .withMisfireHandlingInstructionDoNothing();
        Trigger trigger = TriggerBuilder.newTrigger().startNow().withIdentity(taskName).withSchedule(cronScheduler).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 刪除任務
     */
    public void delJob(String taskName) throws SchedulerException {
        System.out.println("taskName : " + taskName);
        TriggerKey triggerKey = TriggerKey.triggerKey(taskName);
        JobKey jobKey = JobKey.jobKey(taskName);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(jobKey);
    }
}
