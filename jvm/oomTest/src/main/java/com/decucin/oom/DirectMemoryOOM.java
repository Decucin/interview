package com.decucin.oom;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/*
 * VM Args: -Xmx20M -XX:MaxDirectMemorySize=10M
 * 通过后一个参数指定最值，默认与Heap Size一致
 */
public class DirectMemoryOOM {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true){
            unsafe.allocateMemory(_1MB);
        }
    }

}

/*
out::
Exception in thread "main" java.lang.OutOfMemoryError
	at sun.misc.Unsafe.allocateMemory(Native Method)
	at com.decucin.oom.DirectMemoryOOM.main(DirectMemoryOOM.java:20)
 */
