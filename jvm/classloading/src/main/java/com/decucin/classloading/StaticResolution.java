package com.decucin.classloading;


// 用javap编译此代码会发现其是通过invokestatic命令调用此方法
// 而且调用的方法版本在编译时以常量池的形式固化到字节码指令参数中
public class StaticResolution {

    public static void sayHello(){
        System.out.println("hello world!");
    }

    public static void main(String[] args) {
        StaticResolution.sayHello();
    }

}
