package org.kesar.lazy.lazydb.util;

import org.kesar.lazy.lazydb.annotate.ID;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * ID 工具类
 * Created by kesar on 16-11-11.
 */

public class IDUtil {
    private final static String ID = "id";

    /**
     * 获取id 字段
     * @param clazz class
     * @return null is not found
     */
    public static Field getIDField(Class<?> clazz){
        return getIDField(ReflectUtil.getDeclaredFields(clazz));
    }

    /**
     * 获取id 字段
     * @param fields class.getDeclaredFields()
     * @return null is not found
     */
    public static Field getIDField(Field[] fields) {
        Field idField=null;
        if (fields != null && fields.length != 0) {
            // 1. 寻找id注解的field，并检查id注解是否只有一个
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                    continue;
                }
                ID id = field.getAnnotation(ID.class);
                if (id != null) {
                    if(idField==null){
                        idField=field;
                    }
                    else{
                        throw new IllegalStateException("a class must have only one ID annotation");
                    }
                }
            }
            // 2. 没有id注解，只能用field名来查找
            if(idField==null){
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                        continue;
                    }
                    String name = field.getName();
                    if (ID.equals(name)) {
                        return field;
                    }
                }
            }
        }
        return idField;
    }
}