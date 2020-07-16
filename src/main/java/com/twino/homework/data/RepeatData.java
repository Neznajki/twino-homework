package com.twino.homework.data;

import java.util.HashMap;

public class RepeatData {
    HashMap<String, RepeatCounter> collection = new HashMap<>();

    public synchronized void loanCreated(String countryCode) {
        if (! collection.containsKey(countryCode)) {
            collection.put(countryCode, new RepeatCounter());
        }

        collection.get(countryCode).increment();
    }

    public synchronized void loanCreationError(String countryCode) {
        collection.get(countryCode).decrement();
    }

    public synchronized Integer getRepeatCount(String countryCode) {
        if (! collection.containsKey(countryCode)) {
            return 0;
        }

        return collection.get(countryCode).getCounter();
    }

    public synchronized void sumData(RepeatData otherCollection) {
        for (String countryCode : otherCollection.collection.keySet()) {
            RepeatCounter newRepeatCounter = otherCollection.collection.get(countryCode);
            if (! collection.containsKey(countryCode)) {
                collection.put(countryCode, new RepeatCounter());
            }

            collection.get(countryCode).sum(newRepeatCounter);
        }
    }
}
