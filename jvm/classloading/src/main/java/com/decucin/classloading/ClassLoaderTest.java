package com.decucin.classloading;

import java.io.IOException;
import java.io.InputStream;

/*
* class com.decucin.classloading.ClassLoaderTest
* false
* */
public class ClassLoaderTest {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if(is == null){
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Object obj = myLoader.loadClass("com.decucin.classloading.ClassLoaderTest").newInstance();

        // 这里会发现，两者全限定名一致，但是由于类加载器不同，所以instanceof标识为false
        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.decucin.classloading.ClassLoaderTest);
    }

}
