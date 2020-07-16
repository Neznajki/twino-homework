package com.twino.homework.data;

public class RepeatCounter {
    Integer counter = 0;
    Integer additional = 0;

    public void increment() {
        counter++;
    }

    public void decrement() {
        counter--;
    }

    public Integer getCounter() {
        return counter + additional;
    }

    public void sum(RepeatCounter repeatCounter) {
        if (this != repeatCounter) {
            additional += repeatCounter.counter;
        }
    }
}
