package org.noear.solonjt.engine.javascript;

import jdk.nashorn.internal.objects.annotations.Setter;
import org.noear.snack.core.exts.ThData;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.engine.EngineFactory;
import org.noear.solonjt.engine.IJtEngine;
import org.noear.solonjt.model.AFileModel;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

/**
 * javascript 代码运行工具
 * */
public class JavascriptJtEngine implements IJtEngine{
    private static final ThData<StringBuilder> _tlBuilder = new ThData(new StringBuilder(1024*5));
    private static final String _lock ="";
    private static JavascriptJtEngine _g;
    public static JavascriptJtEngine singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new JavascriptJtEngine();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Invocable    _eng_call;
    private final Set<String>  _loaded_names;

    private JavascriptJtEngine(){
        _loaded_names = Collections.synchronizedSet(new HashSet<>());

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("nashorn");
        _eng_call = (Invocable)_eng;

        XApp.global().shared().forEach((k, v)->{
            sharedSet(k, v);
        });

        XApp.global().onSharedAdd((k,v)->{
            sharedSet(k, v);
        });

        sharedSet("__JTEAPI", new __JTEAPI());

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("var __global={lib:{}};");

            sb.append("Date.prototype.toJSON =function(){ return this.getTime()};");

            sb.append("var XContext = Java.type('org.noear.solon.core.XContext');");
            sb.append("var ONode = Java.type('org.noear.snack.ONode');");

            sb.append("var Datetime = Java.type('org.noear.solonjt.utils.Datetime');");
            sb.append("var Timecount = Java.type('org.noear.solonjt.utils.Timecount');");
            sb.append("var Timespan = Java.type('org.noear.solonjt.utils.Timespan');");

            sb.append("function modelAndView(tml,mod){return __JTEAPI.modelAndView(tml,mod);};");

            sb.append("function require(path){__JTEAPI.require(path);return __global.lib[path]}");

            //为JSON.stringify 添加java的对象处理

            sb.append("function stringify_java(k,v){if(v){if(v.getTicks){return v.getTicks()}if(v.getTime){return v.getTime()}if(v.putAll){var obj={};v.forEach(function(k2,v2){obj[k2]=v2});return obj}if(v.addAll){var ary=[];v.forEach(function(v2){ary.push(v2)});return ary}}return v};");

            sb.append("function API_RUN(api){var rst=api(XContext.current());if(rst){if(typeof(rst)=='object'){return JSON.stringify(rst,stringify_java)}else{return rst}}else{return null}};");

            _eng.eval(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }

    public void sharedSet(String name,Object val){
        _eng.put(name, val);
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "javascript";
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
    public  void del(String name) {
        String name2 = name.replace(".", "_");
        _loaded_names.remove(name2);
        _loaded_names.remove(name2 + "__lib");
    }

    @Override
    public  void delAll() {
        _loaded_names.clear();
    }

    @Override
    public  Object exec(String name, AFileModel file, XContext ctx, Map<String,Object> model, boolean outString) throws Exception {
        String name2 = name.replace(".","_");

        preLoad(name2, file);

        if(outString){
            Object api = _eng.get("API_"+name2);
            Object tmp = _eng_call.invokeFunction("API_RUN",api);

            if (tmp == null) {
                return null;
            } else {
                return tmp.toString();
            }
        }else{
            return _eng_call.invokeFunction("API_"+name2, ctx);
        }
    }



    //////////////////////////////////////////////////////////////////


    /**
     * 编译为函数代码
     * */
    public  String compilerAsFun(String name, AFileModel file) {
        StringBuilder sb = _tlBuilder.get();
        sb.setLength(0);

        sb.append("this.API_").append(name).append("=function(ctx){");
        sb.append("\r\n\r\n");
        sb.append(file.content);
        sb.append("\r\n\r\n};");

        if (name.endsWith("__lib")) {
            sb.append("__global.lib['")
                    .append(file.path)
                    .append("']=")
                    .append("new API_")
                    .append(name)
                    .append("();");
        }

        return sb.toString();
    }

    //////////////////////////////////////////////////////////////////
    /**
     * javascript 引擎嵌入接口
     * */
    public static class __JTEAPI {
        public String require(String path) throws Exception {
            String name = path.replace("/", "__");
            String name2 = name.replace(".", "_") + "__lib";

            AFileModel file = EngineFactory.fileGet(path);

            JavascriptJtEngine.singleton().preLoad(name2, file);

            return name2;
        }

        public Object modelAndView(String path, Map<String, Object> model) throws Exception {
            String path2 = path;//AFileUtil.path2(path);//不用转为*
            String name = path2.replace("/", "__");

            AFileModel file = EngineFactory.fileGet(path2);

            if (file.file_id > 0) {
                return EngineFactory.call(name,file,XContext.current(),model,true);
            } else {
                return "";
            }
        }
    }
}
