package com.decucin.classloading;


/*
*
* 123
* */
public class NotInitialization3 {

    // 这里只进行了父类的初始化
    public static void main(String[] args) {
        System.out.println(ConstClass.value);
    }

}
