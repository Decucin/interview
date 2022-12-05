package com.decucin.classloading;

import static java.lang.invoke.MethodHandles.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class MethodHandleTest {

    static class ClassA{
        public void println(String s){
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws Throwable {
        Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        // 能够执行
        getPrintlnMH(obj).invokeExact("icyfenix");
    }

    private static MethodHandle getPrintlnMH(Object receiver) throws Throwable{
        // MethodType: 包含方法返回值以及具体参数
        MethodType mt = MethodType.methodType(void.class, String.class);
        // 在指定类中查找符合给定的方法名称、类型以及符合的句柄
        // bindTo绑定this的指向
        return lookup().findVirtual(receiver.getClass(), "println", mt).bindTo(receiver);
    }

}
