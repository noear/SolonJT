package org.noear.solonjt.executor.s.python;

import org.noear.snack.ONode;
import org.noear.snack.core.utils.NodeUtil;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.executor.IJtExecutor;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.Datetime;
import org.noear.solonjt.utils.Timecount;
import org.noear.solonjt.utils.Timespan;
import org.noear.weed.ext.ThData;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

public class PythonJtExecutor implements IJtExecutor {
    private static final ThData<StringBuilder> _tlBuilder = new ThData(new StringBuilder(1024 * 5));
    private static final String _lock = "";
    private static PythonJtExecutor _g;

    public static PythonJtExecutor singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new PythonJtExecutor();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Invocable    _eng_call;
    private final Set<String>  _loaded_names;

    private PythonJtExecutor() {
        _loaded_names = Collections.synchronizedSet(new HashSet<>());

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("python");
        _eng_call = (Invocable) _eng;

        XApp.global().shared().forEach((k, v) -> {
            sharedSet(k, v);
        });

        XApp.global().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });

        sharedSet("__JTEAPI", new __JTEAPI_CLZ());

        sharedSet("XContext", XContext.class);
        sharedSet("ONode", ONode.class);

        sharedSet("Datetime", Datetime.class);
        sharedSet("Timecount", Timecount.class);
        sharedSet("Timespan", Timespan.class);

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("__global = {'lib':{}}\n\n");

            sb.append("def modelAndView(tml,mod):\n" +
                    "    return __JTEAPI.modelAndView(tml,mod)\n\n");

            sb.append("def require(path):\n" +
                    "    __JTEAPI.require(path)\n" +
                    "    return __global['lib'][path]\n\n");

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
        return "python";
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

        Object tmp = _eng_call.invokeFunction("API_" + name2, ctx);

        if (tmp == null) {
            return null;
        } else {
            if (outString) {
                if (tmp instanceof Number || tmp instanceof String || tmp instanceof Boolean){
                    return tmp.toString();
                }else{
                    return NodeUtil.fromObj(tmp).toJson();
                }
            }else{
                return tmp;
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


        if (name.endsWith("__lib")) {
            sb.append("class API_").append(name).append(":\n");

            for (int i = 0, len = lines.length; i < len; i++) {
                sb.append("    ").append(lines[i]).append("\n");
            }
            sb.append("\n\n");

            sb.append("__global['lib']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append("()");

        } else {
            sb.append("def API_").append(name).append("(ctx):\n");
            for (int i = 0, len = lines.length; i < len; i++) {
                sb.append("    ").append(lines[i]).append("\n");
            }
        }


        return sb.toString();
    }
}
