package com.decucin.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

// 200000
public class AtomicTest {

    private static AtomicInteger race = new AtomicInteger(0);

    public static void increase(){
        race.incrementAndGet();
    }

    public static final int THREAD_COUNT = 20;

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i){
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; ++j){
                    increase();
                }
            });
            threads[i].start();
        }

        while (Thread.activeCount() > 2){
            Thread.yield();
        }
        System.out.println(race);
    }
}
