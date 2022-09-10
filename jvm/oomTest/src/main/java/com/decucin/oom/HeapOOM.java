package com.decucin.oom;

import java.util.ArrayList;
import java.util.List;

/*
 * 堆的OOM实验，注意虚拟机参数不可忽略
 * VM args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 */
public class HeapOOM {

    static class OOMObj{

    }

    public static void main(String[] args) {
        // 保证到GCRoot有连接，即生产的对象不会被GC
        List<OOMObj> list = new ArrayList<OOMObj>();
        while (true){
            list.add(new OOMObj());
        }
    }
}
/*
output::
java.lang.OutOfMemoryError: Java heap space
        Dumping heap to java_pid49952.hprof ...
        Heap dump file created [28186231 bytes in 0.103 secs]
        Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.util.Arrays.copyOf(Arrays.java:3210)
        at java.util.Arrays.copyOf(Arrays.java:3181)
        at java.util.ArrayList.grow(ArrayList.java:267)
        at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:241)
        at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:233)
        at java.util.ArrayList.add(ArrayList.java:464)
        at com.decucin.oom.HeapOOM.main(HeapOOM.java:20)
*/
