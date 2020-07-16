package com.twino.homework.service;

import com.twino.homework.data.RepeatData;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Service
public class RepeatServiceImpl {
    public static Integer intervalSeconds = 5;
    public static Integer maxRepeats = 2;
    protected HashMap<Long, RepeatData> repeatDataHashMap = new HashMap<>();

    public void cleanup() {
        long seconds = Instant.now().getEpochSecond();
        Collection<Long> outdated = new ArrayList<>();
        for (Long created: repeatDataHashMap.keySet()) {

            if (created < seconds - intervalSeconds) {
                outdated.add(created);
            }
        }

        repeatDataHashMap.keySet().removeAll(outdated);
    }

    synchronized public Boolean isRepeatRequestAllowed(String countryCode) {
        RepeatData actualRepeatData = getActualRepeatData();

        boolean isAllowed = actualRepeatData.getRepeatCount(countryCode) < maxRepeats;

        if (isAllowed) {
            actualRepeatData.loanCreated(countryCode);
        }

        return isAllowed;
    }

    synchronized public void loanCreationError(String countryCode) {
        getActualRepeatData().loanCreationError(countryCode);
    }

    synchronized protected RepeatData getActualRepeatData() {
        long seconds = Instant.now().getEpochSecond();
        RepeatData result = repeatDataHashMap.get(seconds);

        if (result == null) {
            result = createNewRepeatCollection(seconds);
        }

        return result;
    }

    private RepeatData createNewRepeatCollection(long seconds) {
        RepeatData result;
        result = new RepeatData();
        repeatDataHashMap.put(seconds, result);

        addIntervalSumToCollection(seconds, result);

        return result;
    }

    private void addIntervalSumToCollection(long seconds, RepeatData result) {
        for (int i=1; i< intervalSeconds; i++) {
            Long index = seconds - i;
            if (! repeatDataHashMap.containsKey(index)) {
                continue;
            }

            result.sumData(repeatDataHashMap.get(index));
        }
    }
}
