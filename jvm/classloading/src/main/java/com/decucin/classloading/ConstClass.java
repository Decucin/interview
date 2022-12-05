package com.decucin.classloading;

public class ConstClass {

    static {
        System.out.println("SuperClass init!");
    }

    // 注意这里有final
    public static final int value = 123;

}
