package com.decucin.classloading;


/*
*
*
* */
public class NotInitialization2 {

    // 这里未进行初始化
    public static void main(String[] args) {
        SuperClass[] sca = new SuperClass[10];
    }

}
