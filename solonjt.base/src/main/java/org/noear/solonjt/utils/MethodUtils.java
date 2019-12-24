package org.noear.solonjt.utils;

import org.noear.solon.annotation.XNote;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodUtils {

    public static List<Map<String, Object>> getMethods(Map<String, Object> kvColl) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        kvColl.forEach((k, v) -> {
            if(v instanceof Class<?>){
                list.add(getMethods(sb,k,(Class<?>) v));
            }else{
                list.add(getMethods(sb,k, v.getClass()));
            }
        });

        return list;
    }

    public static Map<String, Object> getMethods(StringBuilder sb,String k, Class<?> cls) {

        Map<String, Object> v1 = new HashMap<>();

        List<Map<String, Object>> methods = new ArrayList<>();

        v1.put("name", k);
        v1.put("type", cls.getTypeName());
        v1.put("methods", methods);

        if (DbContext.class.isAssignableFrom(cls) || ICacheServiceEx.class.isAssignableFrom(cls)) {
            return v1;
        }

        List<Method> mlist = new ArrayList<>();
        for (Method m : cls.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers()) == false) {
                continue;
            } else {
                mlist.add(m);
            }
        }

        mlist.sort((m1, m2) -> {
            return String.CASE_INSENSITIVE_ORDER.compare(m1.getName(), m2.getName());
        });

        for (Method m : mlist) {

            Map<String, Object> m1 = new HashMap<>();

            XNote tmp = m.getAnnotation(XNote.class);


            if (tmp != null) {
                m1.put("note", "/** " + tmp.value() + " */");
            }

            sb.setLength(0);
            sb.append(k).append(".");
            sb.append(m.getName());

            sb.append("(");

            for (Parameter p : m.getParameters()) {
                sb.append(p.getType().getSimpleName())
                        .append(" ")
                        .append(p.getName())
                        .append(",");
            }

            if (m.getParameterCount() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            sb.append(")");

            if(m.getReturnType() != null){
                sb.append("->")
                        .append(m.getReturnType().getSimpleName());
            }

            m1.put("code", sb.toString());
            methods.add(m1);
        }

        return v1;
    }
}