package com.decucin.classloading;

public class SubClass extends SuperClass{

    static {
        System.out.println("SubClass init!");
    }

}
