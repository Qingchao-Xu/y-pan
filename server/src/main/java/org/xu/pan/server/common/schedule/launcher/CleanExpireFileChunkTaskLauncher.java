package org.xu.pan.server.common.schedule.launcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.xu.pan.schedule.ScheduleManager;
import org.xu.pan.server.common.schedule.task.CleanExpireChunkFileTask;

/**
 * 定时清理过期的文件分片任务触发器
 */
@Slf4j
@Component
public class CleanExpireFileChunkTaskLauncher implements CommandLineRunner {

    private final static String CRON = "1 0 0 * * ? ";
//    private final static String CRON = "0/5 * * * * ? ";

    @Autowired
    private CleanExpireChunkFileTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }

}
