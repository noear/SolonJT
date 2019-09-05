package org.noear.solonjt.dso;

import org.noear.solon.annotation.XNote;
import org.noear.solonjt.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 引擎扩展函数管理器 */
public class XFun {
    public static final XFun g  = new XFun();

    private Map<String, XFunHandler> _xfunMap = new HashMap<>();
    private Map<String, String> _xfunNot = new HashMap<>();

    @XNote("函数设置")
    public void set(String name, XFunHandler fun){
        _xfunMap.put(name,fun);
    }
    @XNote("函数设置（带注释）")
    public void set(String name, String note, XFunHandler fun){
        _xfunMap.put(name,fun);
        _xfunNot.put(name,note);
    }

    @XNote("函数获取")
    public XFunHandler find(String name){ //不能用get；不然，模板可以: XFun.xxx.call({}); //不用于统一
        return _xfunMap.get(name);
    }
    @XNote("函数检查")
    public boolean contains(String name){
        return _xfunMap.containsKey(name);
    }

    @XNote("函数调用")
    public Object call(String name, Map<String,Object> args) throws Exception{ //留着 Exception
        XFunHandler fun = _xfunMap.get(name);
        Object tmp = null;
        if (fun != null) {
            tmp = fun.call(args);
        }

        return tmp;
    }

    public void openList(List<Map<String, Object>> list) {
        Map<String, Object> v1 = new HashMap<>();
        List<Map<String, Object>> methods = new ArrayList<>();

        v1.put("name", "XFun.call(name,args)");
        v1.put("type", "Object");
        v1.put("methods", methods);

        StringBuilder sb = new StringBuilder();

        _xfunNot.forEach((k, v) -> {
            Map<String, Object> m1 = new HashMap<>();
            String[] ss = v.split("#");

            //注解
            m1.put("note", "/** " + ss[0] + " */");

            //代码
            sb.setLength(0);
            sb.append("XFun.call('").append(k).append("',");
            sb.append("{");
            if (ss.length > 1) {
                sb.append(ss[1]);
            } else {
                sb.append("..");
            }
            sb.append("}");
            sb.append("->");
            if (ss.length > 2) {
                sb.append(ss[2]);
            } else {
                sb.append("Object");
            }

            m1.put("code", sb.toString());

            methods.add(m1);
        });

        list.add(v1);
    }
}

