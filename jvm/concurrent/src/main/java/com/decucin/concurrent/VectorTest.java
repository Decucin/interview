package com.decucin.concurrent;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

/*
*
* Exception in thread "Thread-157" java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 10
	at java.util.Vector.get(Vector.java:753)
	at com.decucin.concurrent.VectorTest.lambda$main$1(VectorTest.java:24)
	at java.lang.Thread.run(Thread.java:748)
Exception in thread "Thread-264" java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 7
	at java.util.Vector.remove(Vector.java:836)
	at com.decucin.concurrent.VectorTest.lambda$main$0(VectorTest.java:18)
	at java.lang.Thread.run(Thread.java:748)
	*
*/


public class VectorTest {

    private static Vector<Integer> vector = new Vector<>();

    public static void main(String[] args) {
        while (true){
            for (int i = 0; i < 10; ++i){
                vector.add(i);
            }

            Thread removeThread = new Thread(() -> {
                // 如果想线程安全，那么需要加此代码块，否则会出问题
                synchronized (vector){
                    for (int i = 0; i < vector.size(); ++i) {
                        vector.remove(i);
                    }
                }
            });

            Thread printThread = new Thread(() -> {
                synchronized (vector){
                    for (int i = 0; i < vector.size(); ++i) {
                        System.out.println(vector.get(i));
                    }
                }
            });

            removeThread.start();
            printThread.start();

            // 这里不模拟太多线程，防止操作系统假死
            while (Thread.activeCount() > 20);
        }
    }

}
