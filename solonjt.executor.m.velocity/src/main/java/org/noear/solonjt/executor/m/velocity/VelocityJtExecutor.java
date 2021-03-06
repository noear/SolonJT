package org.noear.solonjt.executor.m.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.executor.IJtExecutor;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;

import javax.script.Bindings;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class VelocityJtExecutor implements IJtExecutor {
    private static final String _lock ="";
    private static VelocityJtExecutor _g;
    public static VelocityJtExecutor singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new VelocityJtExecutor();
                }
            }
        }

        return _g;
    }

    //引擎
    private VelocityEngine _engine = new VelocityEngine();
    private VelocityContext _sharedVariable=new VelocityContext();
    //加载器
    private StringResourceRepositoryEx _loader;

    private VelocityJtExecutor() {
        Properties p = new Properties();
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");

        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
                "org.noear.solonjt.executor.m.velocity.StringResourceLoaderEx");

        p.setProperty("string.resource.loader.repository.class",
                "org.noear.solonjt.executor.m.velocity.StringResourceRepositoryEx");  //这是自定义的获取模板实现

        _engine.init(p);

        _loader = (StringResourceRepositoryEx)StringResourceLoaderEx.getRepository();

        try {
            XApp.global().shared().forEach((k,v)->{
                sharedSet(k,v);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        XApp.global().onSharedAdd((k,v)->{
            sharedSet(k,v);
        });
    }

    public void sharedSet(String name,Object obj){
        try {
            _sharedVariable.put(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public boolean put(String name, AFileModel file){
        if(TextUtils.isEmpty(file.content)){
            return false;
        }else {
            _loader.putStringResource(name, file.content,"utf-8");
            return true;
        }
    }

    public StringResource get(String name) throws Exception{
        return _loader.getStringResource(name);
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "velocity";
    }


    @Override
    public boolean isLoaded(String name){
        return _loader.contains(name);
    }

    @Override
    public boolean preLoad(String name, AFileModel file) throws Exception{
        if (isLoaded(name)) {
            return true;
        }

        return put(name, file);
    }

    @Override
    public void del(String name){
        _loader.removeStringResource(name);
    }

    @Override
    public void delAll(){
        _loader.removeAll();
    }

    @Override
    public Object exec(String name, AFileModel file, XContext ctx, Map<String,Object> model, boolean outString) throws Exception {
        if(preLoad(name,file)){
            if(model == null){
                model = new HashMap<>();
            }

            if(ctx == null){
                model.put("ctx", XContext.current());
            }else{
                model.put("ctx", ctx);
            }

            VelocityContext context = new VelocityContext(model, _sharedVariable);
            Template tmpl = _engine.getTemplate(name, "utf-8");


            StringWriter writer = new StringWriter();

            tmpl.merge(context, writer);

            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
