package com.decucin.classloading;


/*
* SuperClass init!
* 123
* */
public class NotInitialization1 {

    // 这里只进行了父类的初始化
    public static void main(String[] args) {
        System.out.println(SubClass.value);
    }

}
