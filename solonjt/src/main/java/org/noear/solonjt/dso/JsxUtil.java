package org.noear.solonjt.dso;

import org.noear.snack.ONode;
import org.noear.solon.core.XContext;
import org.noear.weed.ext.Fun0Ex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * javascript 代码运行工具
 * */
public class JsxUtil {
    private static String lock="";
    private static JsxUtil _g;
    public static JsxUtil g(){
        if(_g == null){
            synchronized (lock){
                if(_g == null){
                    _g  =new JsxUtil();
                }
            }
        }

        return _g;
    }

    public JsxUtil(){
        _jtSQL = new JsxEngine();
        obj_loaded = Collections.synchronizedList(new ArrayList<>());
    }

    private final JsxEngine _jtSQL;
    private final List<String> obj_loaded;


    private  void loadFunc(String name2, Fun0Ex<String, Exception> code) throws Exception {
        if (isLoaded(name2) == false) {
            obj_loaded.add(name2);

            _jtSQL.eval(code.run());
        }
    }

    private  boolean isLoaded(String name2) {
        return obj_loaded.contains(name2);
    }

    public  void del(String name) {
        String name2 = name.replace(".", "_");
        obj_loaded.remove(name2);
        obj_loaded.remove(name2 + "__lib");
    }

    public  void delAll() {
        obj_loaded.clear();
    }


    public  void tryInitApi(String name2,AFileModel api) throws Exception {
        loadFunc(name2, () -> compilerApiAsFun(name2, api));
    }


    public  Object runApi(String name,AFileModel api, boolean asApi) throws Exception {
        String name2 = name.replace(".","_");

        tryInitApi(name2,api);

        String code = compilerRunApi(name2, asApi);

        return _jtSQL.eval(code);
    }

    public  Object runCode(String code) throws Exception{
        return _jtSQL.eval(code);
    }

    //////////////////////////////////////////////////////////////////

    //::util

    private  ONode buildArgs(XContext context) {
        ONode args = new ONode().asObject();
        context.paramMap().forEach((k,v)->{
            args.set(k, v);
        });

        return args;
    }

    //////////////////////////////////////////////////////////////////


    public  String compilerApiAsFun(String name,AFileModel api) {
        StringBuilder sb = new StringBuilder();

        sb.append("this.API_").append(name).append("=function(ctx){");
        sb.append("\r\n\r\n");
        sb.append(api.content);
        sb.append("\r\n\r\n};");

        if (name.endsWith("__lib")) {
            sb.append("API_")
                    .append(name)
                    .append(".g = ")
                    .append("new API_")
                    .append(name)
                    .append("();");

            sb.append("__global.lib['")
                    .append(api.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append(".g;");
        }

        return sb.toString();
    }

    public  String compilerRunApi(String name, boolean asApi){

        StringBuilder sb = new StringBuilder();

        if(asApi) {
            sb.append("API_RUN(");
            sb.append("this.API_").append(name);
            sb.append(");");
        }else{
            sb.append("this.API_").append(name).append("(XContext.current())");
        }

        return sb.toString();
    }
}
