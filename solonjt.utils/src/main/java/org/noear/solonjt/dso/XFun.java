package org.noear.solonjt.dso;

import org.noear.solon.annotation.XNote;
import org.noear.solonjt.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 引擎扩展函数管理器 */
public class XFun {
    public static final XFun g  = new XFun();

    private Map<String, XFunEntity> _xfunMap = new HashMap<>();


    public void openList(List<Map<String, Object>> list) {
        Map<String, Object> v1 = new HashMap<>();
        List<Map<String, Object>> methods = new ArrayList<>();

        v1.put("name", "XFun.call(name,args)");
        v1.put("type", "Object");
        v1.put("methods", methods);

        StringBuilder sb = new StringBuilder();

        _xfunMap.forEach((k, ent) -> {
            if(TextUtils.isEmpty(ent.note)){
                return;
            }

            Map<String, Object> m1 = new HashMap<>();
            String[] ss = ent.note.split("#");

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
            sb.append("})");
            sb.append("->");

            //此处别再动了
            if (ss.length > 2) {
                sb.append(ss[2]);
            }else{
                sb.append("Object");
            }

            m1.put("code", sb.toString());

            methods.add(m1);
        });

        list.add(v1);
    }

    @XNote("函数设置")
    public void set(String name, XFunHandler fun){
        set(name,null,0,fun);
    }
    @XNote("函数设置（带注释）")
    public void set(String name, String note, XFunHandler fun){
        set(name,note,0,fun);
    }
    @XNote("函数设置（带注释、优先级）")
    public void set(String name, String note, int priority, XFunHandler fun) {
        XFunEntity ent = _xfunMap.get(name);
        if (ent != null && ent.priority > priority) {
            return;
        }

        if(ent == null) {
            ent = new XFunEntity();
            ent.set(fun, priority, note);
            _xfunMap.put(name, ent);
        }else{
            ent.set(fun, priority, note);
        }
    }

    @XNote("函数获取")
    public XFunHandler find(String name) { //不能用get；不然，模板可以: XFun.xxx.call({}); //不用于统一
        return _xfunMap.get(name);
    }

    @XNote("函数检查")
    public boolean contains(String name){
        return _xfunMap.containsKey(name);
    }

    @XNote("函数调用")
    public Object tryCall(String name, Map<String,Object> args) {
        XFunHandler fun = _xfunMap.get(name);

        Object tmp = null;
        if (fun != null) {
            try {
                tmp = fun.call(args);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return tmp;
    }

    @XNote("函数调用")
    public Object call(String name, Map<String,Object> args) throws Exception{ //留着 Exception
        return callT(name,args);
    }

    @XNote("函数调用")
    public <T> T callT(String name, Map<String,Object> args) throws Exception{ //留着 Exception
        XFunHandler fun = _xfunMap.get(name);

        Object tmp = null;
        if (fun != null) {
            tmp = fun.call(args);
        }

        return (T)tmp;
    }

    @XNote("调用一个文件")
    public Object callFile(String path) throws Exception {
        return CallUtil.callFile(path ,null);
    }

    @XNote("调用一个文件")
    public Object callFile(String path, Map<String,Object> attrs) throws Exception {
        return CallUtil.callFile(path, attrs);
    }

    @XNote("调用一组勾子")
    public String callLabel(String tag, String label, boolean useCache) throws Exception{
        return CallUtil.callLabel(tag, label, useCache, null);
    }

    @XNote("调用一组勾子")
    public String callLabel(String tag,String label, boolean useCache, Map<String,Object> attrs) throws Exception{
        return CallUtil.callLabel(tag, label, useCache, attrs);
    }

}
