package org.noear.solonjt.dso;


import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

/**
 * javascript 引敬的封装
 * */
public class JsxEngine {
    private ScriptEngine jsEngine = null;

    public JsxEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        jsEngine = scriptEngineManager.getEngineByName("nashorn");

        XApp.global().shared().forEach((k,v)->{
            sharedSet(k, v);
        });

        XApp.global().onSharedAdd((k,v)->{
            sharedSet(k, v);
        });

        sharedSet("JTAPI", new JTAPI());

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("var __global={lib:{}};");

            sb.append("Date.prototype.toJSON =function(){ return this.getTime()};");

            sb.append("var XContext = Java.type('org.noear.solon.core.XContext');");
            sb.append("var ONode = Java.type('org.noear.snack.ONode');");

            sb.append("var Datetime = Java.type('org.noear.solonjt.utils.Datetime');");
            sb.append("var Timecount = Java.type('org.noear.solonjt.utils.Timecount');");
            sb.append("var Timespan = Java.type('org.noear.solonjt.utils.Timespan');");

            sb.append("function modelAndView(tml,mod){return JTAPI.modelAndView(tml,mod);};");
            //sb.append("function require(path){var c=JTAPI.require(path); return eval(c);}");
            sb.append("function require(path){JTAPI.require(path);return __global.lib[path]}");

            //为JSON.stringify 添加java的对象处理
            //sb.append("function stringify_java(k,v){if(v){if(v.getTicks){return v.getTicks()}if(v.getTime){return v.getTime()}if(v.addAll||v.putAll){return JSON.parse(JTAPI.serialize_obj(v))}}return v};");
            sb.append("function stringify_java(k,v){if(v){if(v.getTicks){return v.getTicks()}if(v.getTime){return v.getTime()}if(v.putAll){var obj={};v.forEach(function(k2,v2){obj[k2]=v2});return obj}if(v.addAll){var ary=[];v.forEach(function(v2){ary.push(v2)});return ary}}return v};");
            //sb.append("function API_RUN(api){var rst=api(XContext.current());if(rst){if(typeof(rst)=='object'){if(rst.addAll||rst.putAll){return JTAPI.serialize_obj(rst)}else{return JSON.stringify(rst,stringify_java)}}else{return rst}}else{return null}};");
            sb.append("function API_RUN(api){var rst=api(XContext.current());if(rst){if(typeof(rst)=='object'){return JSON.stringify(rst,stringify_java)}else{return rst}}else{return null}};");
            eval(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sharedSet(String name,Object val){
        jsEngine.put(name, val);
    }

    public Object eval(String code) throws ScriptException {
        return jsEngine.eval(code);
    }

    public class JTAPI {
        public String serialize_obj(Object obj) throws Exception {
            if (obj instanceof String) {
                return (String) obj;
            }

            return ONode.map(obj).toJson();
        }

        public String require(String path) throws Exception {
            String name = path.replace("/", "__");
            String name2 = name.replace(".", "_") + "__lib";

            AFileModel file = AFileUtil.get(path);

            JsxUtil.g().tryInitApi(name2, file);

            return name2;

//            return new StringBuilder()
//                    .append("this.API_")
//                    .append(name2)
//                    .append(".g")
//                    .toString();
        }

        public Object modelAndView(String path, Map<String, Object> model) throws Exception {
            String path2 = path;//AFileUtil.path2(path);//不用转为*
            String name = path2.replace("/", "__");

            AFileModel file = AFileUtil.get(path2);

            if (file.file_id > 0) {
                model.put("ctx", XContext.current());
                return FtlUtil.g().reander(name,file, model);
            } else {
                return "";
            }
        }

    }
}
