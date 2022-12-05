package com.decucin.classloading;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


// welcome
// Hello world!
// 动态代理的简单使用
public class DynamicProxyTest {

    interface IHello{
        void sayHello();
    }

    static class Hello implements IHello{

        @Override
        public void sayHello() {
            System.out.println("Hello world!");
        }
    }

    static class DynamicProxy implements InvocationHandler{

        Object originObj;

        Object bind(Object originObj){
            this.originObj = originObj;
            return Proxy.newProxyInstance(originObj.getClass().getClassLoader(), originObj.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("welcome");
            return method.invoke(originObj, args);
        }
    }

    public static void main(String[] args) {
        IHello hello = (IHello) new DynamicProxy().bind(new Hello());
        hello.sayHello();
    }

}
