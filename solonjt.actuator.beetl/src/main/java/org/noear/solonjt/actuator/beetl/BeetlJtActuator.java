package org.noear.solonjt.actuator.beetl;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.actuator.IJtActuator;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class BeetlJtActuator implements IJtActuator {
    private static final String _lock ="";
    private static BeetlJtActuator _g;
    public static BeetlJtActuator singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new BeetlJtActuator();
                }
            }
        }

        return _g;
    }

    Configuration cfg = null;
    GroupTemplate _engine;
    MapResourceLoaderEx tmls;

    private BeetlJtActuator() {
        try {
            cfg = Configuration.defaultConfiguration();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        cfg.setIgnoreClientIOError(true);

        tmls  = new MapResourceLoaderEx();

        _engine = new GroupTemplate(tmls,cfg);

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
            //_engine : GroupTemplate
            _engine.getSharedVars().put(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public boolean put(String name, AFileModel file){
        if(TextUtils.isEmpty(file.content)){
            return false;
        }else {
            tmls.put(name, file.content);
            return true;
        }
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "beetl";
    }


    @Override
    public boolean isLoaded(String name){
        return tmls.containsKey(name);
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
        tmls.remove(name);
    }

    @Override
    public void delAll(){
        tmls.clear();
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


            Template template = _engine.getTemplate(name);
            template.binding(model);

            StringWriter writer = new StringWriter();


            template.renderTo(writer);

            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
