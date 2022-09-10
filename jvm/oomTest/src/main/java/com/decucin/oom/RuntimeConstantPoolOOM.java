package com.decucin.oom;


import java.util.HashSet;
import java.util.Set;

/*
 * JDK 6及之前的版本
 * Vm args: -XX:PermSize=6M -XX:MaxPerSize=6M
 */
public class RuntimeConstantPoolOOM {

    public static void main(String[] args) {
        // 使用Set保持常量池引用，防止被Full GC
        Set<String> set = new HashSet<String>();
        short i = 0;
        while(true){
            set.add(String.valueOf(i++).intern());
        }
    }

}
/*
 * JDK 6及其之前会OOM
 */
