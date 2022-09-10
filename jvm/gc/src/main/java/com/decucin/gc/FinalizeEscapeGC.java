package com.decucin.gc;

/*
 * 对象在被GC时可以自救，有且只有一次机会
 */
public class FinalizeEscapeGC {

    public static FinalizeEscapeGC SAVEHOCK = null;

    public void isAlive(){
        System.out.println("yes, still alive!");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method executed!");
        SAVEHOCK = this;
    }

    public static void main(String[] args) throws InterruptedException {
        SAVEHOCK = new FinalizeEscapeGC();

        // 第一次成功自救
        SAVEHOCK = null;
        System.gc();
        // Finalizer优先级低，等一等
        Thread.sleep(500);
        if(SAVEHOCK != null){
            SAVEHOCK.isAlive();
        }else {
            System.out.println("no, dead");
        }

        // 第二次自救失败
        SAVEHOCK = null;
        System.gc();
        // Finalizer优先级低，等一等
        Thread.sleep(500);
        if(SAVEHOCK != null){
            SAVEHOCK.isAlive();
        }else {
            System.out.println("no, dead");
        }
    }
}

/*
out:
finalize method executed!
yes, still alive!
no, dead
*/