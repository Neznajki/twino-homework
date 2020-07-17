package com.twino.homework.scheduler;

import com.twino.homework.service.RepeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CleanupRepeatsTaskTest {
    @Mock
    CleanupRepeatsTask cleanupRepeatsTask;
    @Mock
    RepeatServiceImpl repeatService;
    @Mock
    Logger logger;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(cleanupRepeatsTask, "repeatService", repeatService);
        ReflectionTestUtils.setField(cleanupRepeatsTask, "logger", logger);
    }

    @Test
    public void doRunTest() {
        doCallRealMethod().when(cleanupRepeatsTask).doRun();

        cleanupRepeatsTask.doRun();

        verify(logger, times(1)).info("cleanup done");
        verify(repeatService, times(1)).cleanup();
    }

}
