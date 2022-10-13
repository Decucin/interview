package reflect;

import java.lang.reflect.Field;

public class ReflectTest {

    public static void main(String[] args) {
        try {
            Class<?> clazz = Class.forName("com.decucin.gen.Obj");
            Obj obj = (Obj) clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 这里这个，只有对存在访问限制的加这个才行
                // 而且加了不一定生效
                // 因为可能虚拟机SecurityManager会阻止此操作
                field.setAccessible(true);
                field.set(obj, "decucin");
                System.out.println(obj.getName());
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

}
