package com.twino.homework.data;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
//TODO add synchronized flag test
public class RepeatDataTest {

    RepeatData repeatData = new RepeatData();

    @Test
    public void loanCreatedTest() {
        assertFalse(repeatData.collection.containsKey("LV"));
        repeatData.loanCreated("LV");
        assertTrue(repeatData.collection.containsKey("LV"));
        assertEquals(1, repeatData.collection.get("LV").counter);

        assertFalse(repeatData.collection.containsKey("GB"));
        repeatData.loanCreated("GB");
        assertTrue(repeatData.collection.containsKey("GB"));
        assertEquals(1, repeatData.collection.get("GB").counter);

        repeatData.loanCreated("LV");
        assertTrue(repeatData.collection.containsKey("LV"));
        assertEquals(2, repeatData.collection.get("LV").counter);
    }

    @Test
    public void loanCreationErrorTest() {
        repeatData.loanCreated("LV");
        repeatData.loanCreated("LV");
        repeatData.loanCreated("LV");
        repeatData.loanCreationError("LV");

        assertEquals(2, repeatData.collection.get("LV").counter);
    }

    @Test
    public void getRepeatCountTest() {
        assertEquals(0, repeatData.getRepeatCount("LV"));
        assertFalse(repeatData.collection.containsKey("LV"));

        RepeatCounter repeatCounter = Mockito.mock(RepeatCounter.class);
        repeatData.collection.put("LV", repeatCounter);

        when(repeatCounter.getCounter()).thenReturn(11);
        assertEquals(11, repeatData.getRepeatCount("LV"));
    }

    @Test
    public void sumDataTest() {
        HashMap<String, RepeatCounter> collection = new HashMap<>();
        RepeatData otherData = new RepeatData();
        HashMap<String, RepeatCounter> otherCollection = new HashMap<>();

        RepeatCounter
            lvCountryA = new RepeatCounter(),
            lvCountryB = new RepeatCounter(),
            ruCountryA = new RepeatCounter(),
            gbCountryB = new RepeatCounter();

        collection.put("LV", lvCountryA);
        lvCountryA.counter = 4;
        lvCountryA.additional = 1;
        collection.put("RU", ruCountryA);
        ruCountryA.counter = 33;
        ruCountryA.additional = 55;

        otherCollection.put("LV", lvCountryB);
        lvCountryB.counter = 55;
        lvCountryB.additional = 11;
        otherCollection.put("GB", gbCountryB);
        gbCountryB.counter = 111;
        gbCountryB.additional = 444;

        repeatData.collection = collection;
        int expectedLv = lvCountryA.counter + lvCountryA.additional + lvCountryB.counter;
        int expectedRu = ruCountryA.counter + ruCountryA.additional;
        Integer expectedGb = gbCountryB.counter;

        otherData.collection = otherCollection;
        repeatData.sumData(otherData);

        assertEquals(expectedLv, repeatData.getRepeatCount("LV"));
        assertEquals(expectedRu, repeatData.getRepeatCount("RU"));
        assertEquals(expectedGb, repeatData.getRepeatCount("GB"));
    }
}
