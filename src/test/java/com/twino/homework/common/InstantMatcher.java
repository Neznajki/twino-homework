package com.twino.homework.common;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstantMatcher {
    long before = Instant.now().getEpochSecond();

    public void wasNow(long data) {
        long after = Instant.now().getEpochSecond();

        assertTrue(data == before || data > before, "time is before process start");
        assertTrue(data == after || data < after, "time is after process finish");
    }
}
