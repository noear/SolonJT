package org.noear.solonjt.actuator.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.actuator.IJtActuator;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class VelocityJtActuator implements IJtActuator {
    private static final String _lock ="";
    private static VelocityJtActuator _g;
    public static VelocityJtActuator singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new VelocityJtActuator();
                }
            }
        }

        return _g;
    }

    private VelocityEngine velocity = new VelocityEngine();
    private VelocityContext _sharedVariable=new VelocityContext();
    private StringResourceRepositoryEx tmls;

    private VelocityJtActuator() {
        Properties p = new Properties();
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");

        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
                "org.noear.solonjt.actuator.velocity.StringResourceLoaderEx");
        p.setProperty("string.resource.loader.repository.class",
                "org.noear.solonjt.actuator.velocity.StringResourceRepositoryEx");  //这是自定义的获取模板实现

        velocity.init(p);

        tmls = (StringResourceRepositoryEx)StringResourceLoaderEx.getRepository();

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
            tmls.putStringResource(name, file.content,"utf-8");
            return true;
        }
    }

    public StringResource get(String name) throws Exception{
        return tmls.getStringResource(name);
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
        return tmls.contains(name);
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
        tmls.removeStringResource(name);
    }

    @Override
    public void delAll(){
        tmls.removeAll();
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
            Template tmpl = velocity.getTemplate(name, "utf-8");


            StringWriter writer = new StringWriter();

            tmpl.merge(context, writer);

            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
