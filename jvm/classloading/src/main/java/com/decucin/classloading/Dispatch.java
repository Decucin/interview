package com.decucin.classloading;


// father choose 360
// son choose QQ
public class Dispatch {

    static class QQ{ }

    static class _360{ }

    public static class Father{
        public void handChoice(QQ arg){
            System.out.println("father choose QQ");
        }

        public void handChoice(_360 arg){
            System.out.println("father choose 360");
        }
    }

    public static class Son extends Father{
        public void handChoice(QQ arg){
            System.out.println("son choose QQ");
        }

        public void handChoice(_360 arg){
            System.out.println("son choose 360");
        }
    }

    public static void main(String[] args) {
        Father father = new Father();
        Father son = new Son();
        father.handChoice(new _360());
        son.handChoice(new QQ());
    }

}
