package com.decucin.classloading;


import static java.lang.invoke.MethodHandles.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public class UseGrandFather {

    class GrandFather{
        void think(){
            System.out.println("i am grandfather");
        }
    }

    class Father extends GrandFather{
        void think(){
            System.out.println("i am father");
        }
    }

    class Son extends Father{
        // 这里如果想要调用GrandFather的方法，不用这种方式很难
        void think(){
            try {
                MethodType mt = MethodType.methodType(void.class);
                Field lookupImpl = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                lookupImpl.setAccessible(true);
                MethodHandle mh = ((MethodHandles.Lookup) lookupImpl.get(null)).findSpecial(GrandFather.class, "thinking", mt, GrandFather.class);
                mh.invoke(this);
            } catch (Throwable e) {

            }
        }
    }

}
