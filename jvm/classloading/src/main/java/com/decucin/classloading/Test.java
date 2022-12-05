package com.decucin.classloading;

// <cinit>()
public class Test {

    static {
        i = 0;  // 赋值能正常通过编译
//        System.out.println(i);    // 这里编译器会提示非法向前引用
    }

    static int i = 1;


    // 局部变量槽的复用对垃圾回收产生了影响
    // 加入运行参数-verbose:gc
    // [GC (System.gc())  70793K->66472K(251392K), 0.0008120 secs]
    // [Full GC (System.gc())  66472K->66236K(251392K), 0.0044201 secs]
    public static void main(String[] args) {
        // 这里加了括号,placeholder的作用域被限定，按理说应该被回收，但实际上没有
        {
            byte[] placeholder = new byte[64 * 1024 * 1024];
        }
//        int a = 0;    // 加入这一行后，结果变为  [GC (System.gc())  70793K->66384K(251392K), 0.0008883 secs]
                                            // [Full GC (System.gc())  66384K->700K(251392K), 0.0044789 secs]
        System.gc();

        // 另一个例子 局部变量不会像类变量一样被赋初值，故这样是不行的
//        int a;
//        System.out.println(a);
    }

    /*
    * 这里内存没有被回收的原因在于：
    * 没有int a = 0 时
    * placeholder原本占用的变量槽没有被复用
    * 局部变量表作为GC Roots的一部分
    * 局部变量表对其有关联
    * 因此回收不了
    * 故有些源码中会存在placeholder = null这样的操作
    * 但是即时编译过程可能会把此代码删除
    * 同时就算没有int a = 0
    * 只要存在即时编译过程
    * 内存也能被正确回收
    * */

}
