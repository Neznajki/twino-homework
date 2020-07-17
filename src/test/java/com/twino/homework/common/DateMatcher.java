package com.twino.homework.common;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateMatcher {
    Timestamp before = new Timestamp(System.currentTimeMillis());

    public void wasNow(Timestamp data) {
        Timestamp after = new Timestamp(System.currentTimeMillis());

        assertTrue(data.equals(before) || data.after(before), "time is before process start");
        assertTrue(data.equals(after) || data.before(after), "time is after process finish");
    }

    public void wasDelay(Timestamp data, int delaySeconds) {
        int compare = data.compareTo(before);
        assertTrue(compare >= delaySeconds);
    }
}
