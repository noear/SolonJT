package org.noear.solonjt.executor.s.ruby;

import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.executor.IJtExecutor;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.ThreadData;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

public class RubyJtExecutor implements IJtExecutor {
    private static final ThreadData<StringBuilder> _tlBuilder = new ThreadData(()->new StringBuilder(1024 * 5));
    private static final String _lock = "";
    private static RubyJtExecutor _g;

    public static RubyJtExecutor singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new RubyJtExecutor();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Invocable    _eng_call;
    private final Set<String>  _loaded_names;

    private RubyJtExecutor() {
        _loaded_names = Collections.synchronizedSet(new HashSet<>());

        //System.setProperty("org.jruby.embed.localvariable.behavior", "global");

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("ruby");
        _eng_call = (Invocable) _eng;

        XApp.global().shared().forEach((k, v) -> {
            sharedSet(k, v);
        });

        XApp.global().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });

        //sharedSet("__JTEAPI", new __JTEAPI());

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("require 'java'\n\n");
            sb.append("java_import org.noear.solon.core.XContext\n");
            sb.append("java_import org.noear.snack.ONode\n");

            sb.append("java_import org.noear.solonjt.utils.Datetime\n");
            sb.append("java_import org.noear.solonjt.utils.Timecount\n");
            sb.append("java_import org.noear.solonjt.utils.Timespan\n");
            sb.append("java_import org.noear.solonjt.executor.s.ruby.JTEAPI_CLZ\n");

            sb.append("\n\n");

            sb.append("$__JTEAPI=JTEAPI_CLZ.new\n\n");

            sb.append("$__global={'lib'=>{},'lib_new'=>{}}\n\n");

            sb.append("def modelAndView(tml,mod)\n" +
                      "    return $__JTEAPI.modelAndView(tml,mod)\n" +
                      "end\n\n");

            sb.append("def requireX(path)\n" +
                    "    if path[1] == '$'\n"+
                    "        path = path[1..]\n"+
                    "        $__JTEAPI.require(path)\n" +
                    "        return $__global['lib_new'][path].NEW1()\n"+
                    "    else\n"+
                    "        $__JTEAPI.require(path)\n" +
                    "        return $__global['lib'][path]\n"+
                    "end\n\n");


            //下面两个将不再支持
            sb.append("def require(path)\n" +
                    "    $__JTEAPI.require(path)\n" +
                    "    return $__global['lib'][path]\n"+
                    "end\n\n");

            sb.append("def requireNew(path)\n" +
                    "    $__JTEAPI.require(path)\n" +
                    "    return $__global['lib_new'][path].NEW1()\n"+
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
        return "ruby";
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


        Object tmp = _eng_call.invokeFunction("API_"+name2, ctx);

        if (tmp == null) {
            return null;
        } else {
            if (outString) {
                if (tmp instanceof Number || tmp instanceof String || tmp instanceof Boolean){
                    return tmp.toString();
                }else{
                    return ONode.loadObj(tmp).toJson();
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
            sb.append("class API_").append(name).append("\n");
            for (int i = 0, len = lines.length; i < len; i++) {
                sb.append("    ").append(lines[i]).append("\n");
            }
            sb.append("end\n\n");

            sb.append("class API_").append(name).append("_NEW\n");
            sb.append("  def NEW1\n");
            sb.append("    return ").append("API_").append(name).append(".new\n");
            sb.append("  end\n");
            sb.append("end\n\n");



            sb.append("$__global['lib']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append(".new\n");

            sb.append("$__global['lib_new']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append("_NEW.new\n");

        } else {
            sb.append("def API_").append(name).append("(ctx)\n");
            for (int i = 0, len = lines.length; i < len; i++) {
                sb.append("    ").append(lines[i]).append("\n");
            }
            sb.append("end\n");
        }


        return sb.toString();
    }




}
