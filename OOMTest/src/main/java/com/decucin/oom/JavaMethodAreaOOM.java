package com.decucin.oom;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/*
 * VM Args: -XX:PermSize=10M -XX:MaxPermSize=10M
 */
public class JavaMethodAreaOOM {

    static class OOMObject{}

    public static void main(final String[] args) {
        while (true){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    return methodProxy.invokeSuper(o, args);
                }
            });
            enhancer.create();
        }
    }

}
/*
 * -XX:MaxMetaspaceSize ->  设置元空间最大值，默认-1不限制
 * -XX:MetaspaceSize    ->  指定元空间的初始空间大小到该值会触发垃圾收集器进行类型卸载，同时收集器会调整该值，释放的多就适当降低该值，反之则适当提高该值(不超过上一个参数)
 * -XX:MinMetaspaceFreeRatio    ->  在垃圾回收之后控制最小的元空间剩余空间百分比，类似的还有    -XX:MaxMetaspaceFreeRatio
 */