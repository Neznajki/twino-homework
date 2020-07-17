package com.twino.homework.data;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RepeatCounterTest {
    RepeatCounter repeatCounter = new RepeatCounter();

    @Test
    public void incrementTest() {
        assertEquals(0, repeatCounter.counter);
        assertEquals(0, repeatCounter.additional);
        repeatCounter.increment();
        assertEquals(1, repeatCounter.counter);
        assertEquals(0, repeatCounter.additional);
    }

    @Test
    public void decrementTest() {
        assertEquals(0, repeatCounter.counter);
        assertEquals(0, repeatCounter.additional);
        repeatCounter.decrement();
        assertEquals(-1, repeatCounter.counter);
        assertEquals(0, repeatCounter.additional);
    }

    @Test
    public void getCounterTest() {
        repeatCounter.counter = 10;
        repeatCounter.additional = 5;

        assertEquals(15, repeatCounter.getCounter());
    }

    @Test
    public void sumTest() {
        repeatCounter.counter = 3;
        repeatCounter.additional = 1;
        RepeatCounter summingElement = new RepeatCounter();
        summingElement.counter = 5;
        summingElement.additional = 11;

        repeatCounter.sum(summingElement);

        assertEquals(3, repeatCounter.counter);
        assertEquals(6, repeatCounter.additional);
    }
}
