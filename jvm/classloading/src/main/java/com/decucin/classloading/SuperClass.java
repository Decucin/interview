package com.decucin.classloading;

public class SuperClass {

    static {
        System.out.println("SuperClass init!");
    }

    // 注意这里没有final
    public static int value = 123;

}
