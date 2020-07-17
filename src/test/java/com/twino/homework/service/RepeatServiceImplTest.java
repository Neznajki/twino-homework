package com.twino.homework.service;

import com.twino.homework.common.InstantMatcher;
import com.twino.homework.data.RepeatData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
//https://genius.com/Owl-city-fireflies-lyrics
public class RepeatServiceImplTest {
    @Mock
    RepeatServiceImpl repeatService;

    @Test
    public void testDefaults() {
        assertEquals(5, RepeatServiceImpl.intervalSeconds);
        assertEquals(2, RepeatServiceImpl.maxRepeats);
    }

    @Test
    public void cleanupTest() {
        repeatService.repeatDataHashMap = new HashMap<>();
        long seconds = Instant.now().getEpochSecond();

        doCallRealMethod().when(repeatService).cleanup();

        long validKey2 = seconds - RepeatServiceImpl.intervalSeconds, outdatedKey = seconds - RepeatServiceImpl.intervalSeconds - 1;
        repeatService.repeatDataHashMap.put(seconds, new RepeatData());
        repeatService.repeatDataHashMap.put(validKey2, new RepeatData());
        repeatService.repeatDataHashMap.put(outdatedKey, new RepeatData());

        when(repeatService.getEpochSecond()).thenReturn(seconds);

        repeatService.cleanup();

        assertThat(repeatService.repeatDataHashMap)
            .hasSize(2)
            .containsKey(seconds)
            .containsKey(validKey2);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void isRepeatRequestAllowedTest(boolean isAllowed) {
        String countryCode = "GG";
        when(repeatService.isRepeatRequestAllowed(countryCode)).thenCallRealMethod();
        RepeatData repeatData = Mockito.mock(RepeatData.class);
        when(repeatService.getActualRepeatData()).thenReturn(repeatData);

        Integer repeats = isAllowed ? RepeatServiceImpl.maxRepeats -1: RepeatServiceImpl.maxRepeats;

        when(repeatData.getRepeatCount(countryCode)).thenReturn(repeats);

        assertEquals(isAllowed, repeatService.isRepeatRequestAllowed(countryCode));

        if (isAllowed) {
            verify(repeatData, times(1)).loanCreated(countryCode);
        } else {
            verify(repeatData, never()).loanCreated(countryCode);
        }

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void getActualRepeatDataTest(boolean exists) {
        //noinspection unchecked
        repeatService.repeatDataHashMap = Mockito.mock(HashMap.class);
        long epochSecond = Instant.now().getEpochSecond();
        when(repeatService.getEpochSecond()).thenReturn(epochSecond);
        RepeatData repeatData = null, newRepeatData = Mockito.mock(RepeatData.class);
        if (exists) {
            repeatData = Mockito.mock(RepeatData.class);
        }

        when(repeatService.getActualRepeatData()).thenCallRealMethod();
        when(repeatService.repeatDataHashMap.get(epochSecond)).thenReturn(repeatData);
        when(repeatService.createNewRepeatCollection(epochSecond)).thenReturn(newRepeatData);

        assertEquals(exists ? repeatData : newRepeatData, repeatService.getActualRepeatData());

        verify(repeatService.repeatDataHashMap, times(1)).get(epochSecond);
    }

    @Test
    public void getEpochSecondTest() {
        when(repeatService.getEpochSecond()).thenCallRealMethod();
        InstantMatcher instantMatcher = new InstantMatcher();

        instantMatcher.wasNow(repeatService.getEpochSecond());
    }

    @Test
    public void createNewRepeatCollectionTest() {
        long seconds = Instant.now().getEpochSecond();
        //noinspection unchecked
        repeatService.repeatDataHashMap = Mockito.mock(HashMap.class);
        when(repeatService.createNewRepeatCollection(seconds)).thenCallRealMethod();
        RepeatData expectingResult = new RepeatData();

        RepeatData result = repeatService.createNewRepeatCollection(seconds);

        assertEquals(expectingResult, result);

        verify(repeatService, times(1)).addIntervalSumToCollection(seconds, expectingResult);
        verify(repeatService.repeatDataHashMap, times(1)).put(seconds, expectingResult);
    }

    @Test
    public void addIntervalSumToCollectionTest() {
        long seconds = 100, index1 = seconds - 2;
        //noinspection unchecked
        repeatService.repeatDataHashMap = Mockito.mock(HashMap.class);
        RepeatData repeatDataNew = Mockito.mock(RepeatData.class), repeatData = Mockito.mock(RepeatData.class);
        doCallRealMethod().when(repeatService).addIntervalSumToCollection(seconds, repeatData);
        when(repeatService.repeatDataHashMap.containsKey(index1)).thenReturn(true);
        when(repeatService.repeatDataHashMap.get(index1)).thenReturn(repeatDataNew);

        repeatService.addIntervalSumToCollection(seconds, repeatData);

        verify(repeatData).sumData(repeatDataNew);
    }
}
