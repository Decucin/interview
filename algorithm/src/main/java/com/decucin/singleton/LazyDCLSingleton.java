package com.decucin.singleton;

public class LazyDCLSingleton {

    private static volatile LazyDCLSingleton instance;

    private LazyDCLSingleton(){}

    public static LazyDCLSingleton getInstance(){
        if(instance == null){
            synchronized (instance){
                if(instance == null){
                    instance = new LazyDCLSingleton();
                }
            }
        }
        return instance;
    }

}
