package com.decucin.gc;

public class ReferenceCountingGC {

    public Object instance = null;

    private static int _1MB = 1024 * 1024;

    // 便于GC日志分析
    private byte[] bigSize = new byte[2 * _1MB];

    public static void main(String[] args) {
        ReferenceCountingGC objA = new ReferenceCountingGC();
        ReferenceCountingGC objB = new ReferenceCountingGC();

        // 这里是循环引用
        objA.instance = objB;
        objB.instance = objA;

        //看有没有被GC
        System.gc();
    }
}
