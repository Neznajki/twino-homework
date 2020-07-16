package com.twino.homework.scheduler;

import com.twino.homework.service.RepeatServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class CleanupRepeatsTask {
    RepeatServiceImpl repeatService;
    Logger logger = LoggerFactory.getLogger(CleanupRepeatsTask.class);

    @Autowired
    public CleanupRepeatsTask(RepeatServiceImpl repeatService) {
        this.repeatService = repeatService;
    }

    @Scheduled(fixedRate = 1000)
    public void doRun() {
        repeatService.cleanup();
        logger.info("cleanup done");
    }
}
