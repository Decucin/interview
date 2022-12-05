package com.decucin.concurrent;


// 结果必然小于10000 * THREADS_COUNT
public class VolatileTest {

    public static volatile int race = 0;

    public static void increase(){
        ++race;
    }

    public static final int THREADS_COUNT = 20;

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];
        for (int i = 0; i < THREADS_COUNT; ++i){
            threads[i] = new Thread(() -> {
               for (int j = 0; j < 10000; ++j){
                   increase();
               }
            });
            threads[i].start();
        }

        // 这里是因为IDEA会自动创建一个线程
        while (Thread.activeCount() > 2){
            Thread.yield();
        }
        System.out.println(race);
    }

}
