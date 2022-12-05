package com.decucin.classloading;

// Thread[main,5,main]init DeadLoopClass!
// 这里陷入了死循环
// 另一个线程一直在等待前一个线程完成加载操作
public class DeadLoopClass {

    static {
        if(true){   //不加这个if会报错
            System.out.println(Thread.currentThread() + "init DeadLoopClass!");
            while (true){

            }
        }
    }

    public static void main(String[] args) {
        Runnable script = new Runnable(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread() + "start!");
                DeadLoopClass dlc = new DeadLoopClass();
                System.out.println(Thread.currentThread() + "over!");
            }
        };

        Thread thread1 = new Thread(script);
        Thread thread2 = new Thread(script);
        thread1.start();
        thread2.start();
    }

}
