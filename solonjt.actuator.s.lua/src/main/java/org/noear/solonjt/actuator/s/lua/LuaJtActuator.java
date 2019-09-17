package org.noear.solonjt.actuator.s.lua;

import org.noear.snack.ONode;
import org.noear.snack.core.utils.NodeUtil;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.actuator.IJtActuator;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.Datetime;
import org.noear.solonjt.utils.Timecount;
import org.noear.solonjt.utils.Timespan;
import org.noear.weed.ext.ThData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

public class LuaJtActuator implements IJtActuator {
    private static final ThData<StringBuilder> _tlBuilder = new ThData(new StringBuilder(1024 * 5));
    private static final String _lock = "";
    private static LuaJtActuator _g;

    public static LuaJtActuator singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new LuaJtActuator();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Set<String>  _loaded_names;

    private LuaJtActuator() {
        _loaded_names = Collections.synchronizedSet(new HashSet<>());


        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("luaj");


        XApp.global().shared().forEach((k, v) -> {
            sharedSet(k, v);
        });

        XApp.global().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });

        sharedSet("__JTEAPI", new __JTEAPI_CLZ());
        sharedSet("XContext",XContext.class);
        sharedSet("ONode", ONode.class);

        sharedSet("Datetime", Datetime.class);
        sharedSet("Timecount", Timecount.class);
        sharedSet("Timespan", Timespan.class);

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("require 'org.luaj.vm2.lib.DebugLib'\n\n");

//            sb.append("XContext = luajava.bindClass('org.noear.solon.core.XContext')\n");
//            sb.append("ONode = luajava.bindClass('org.noear.snack.ONode')\n");

            sb.append("\n");
            sb.append("__global = {lib={}}\n\n");

            sb.append("function modelAndView(tml,mod)\n" +
                      "    return __JTEAPI:modelAndView(tml,mod)\n" +
                      "end\n\n");


            sb.append("function require(path)\n" +
                    "    __JTEAPI:require(path)\n" +
                    "    return __global['lib'][path]\n"+
                    "end\n\n");

            _eng.eval(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void sharedSet(String name, Object val) {
        _eng.put(name, val);
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "lua";
    }

    @Override
    public boolean isLoaded(String name2) {
        return _loaded_names.contains(name2);
    }

    @Override
    public boolean preLoad(String name2, AFileModel file) throws Exception {
        if (isLoaded(name2) == false) {
            _loaded_names.add(name2);

            _eng.eval(compilerAsFun(name2, file));
        }

        return true;
    }

    @Override
    public void del(String name) {
        String name2 = name.replace(".", "_").replace("*","_");
        _loaded_names.remove(name2);
        _loaded_names.remove(name2 + "__lib");
    }

    @Override
    public void delAll() {
        _loaded_names.clear();
    }

    @Override
    public Object exec(String name, AFileModel file, XContext ctx, Map<String, Object> model, boolean outString) throws Exception {
        String name2 = name.replace(".", "_").replace("*","_");

        preLoad(name2, file);
//
        Object tmp = _eng.eval("return API_"+name2+"(XContext:current())");

        if (tmp == null) {
            return null;
        } else {
            if (outString) {
                if (tmp instanceof Number || tmp instanceof String || tmp instanceof Boolean){
                    return tmp.toString();
                }else{
                    Object obj = LuaUtil.luaToObj(tmp);
                    return NodeUtil.fromObj(obj).toJson();
                }
            }else{
                return LuaUtil.luaToObj(tmp);
            }
        }
    }


    //////////////////////////////////////////////////////////////////


    /**
     * 编译为函数代码
     */
    public String compilerAsFun(String name, AFileModel file) {
        StringBuilder sb = _tlBuilder.get();
        sb.setLength(0);

        String[] lines = file.content.split("\n");


        sb.append("function API_").append(name).append("(ctx)\n");
        for (int i = 0, len = lines.length; i < len; i++) {
            sb.append("    ").append(lines[i]).append("\n");
        }
        sb.append("end\n");

        if (name.endsWith("__lib")) {
            sb.append("__global['lib']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append("();");
        }

        return sb.toString();
    }
}
