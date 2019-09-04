package org.noear.solonjt.dso;

import org.noear.solon.annotation.XNote;

import java.util.HashMap;
import java.util.Map;

public class XFun {
    public static final XFun g  = new XFun();

    private Map<String, XFunHandler> _xfunMap = new HashMap<>();

    @XNote("函数设置")
    public void set(String name, XFunHandler fun){
        _xfunMap.put(name,fun);
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

//    @XNote("函数调用")
//    public Object call_try(String name, Map<String,Object> args) throws Exception{ //留着 Exception
//        XFunHandler fun = _xfunMap.get(name);
//        Object tmp = null;
//        if (fun != null) {
//            tmp = fun.call(args);
//        }
//
//        return tmp;
//    }
}

