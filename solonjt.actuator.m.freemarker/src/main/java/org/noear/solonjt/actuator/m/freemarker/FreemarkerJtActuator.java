package org.noear.solonjt.actuator.m.freemarker;

import freemarker.cache.MruCacheStorage;
import freemarker.cache.StringTemplateLoaderEx;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.actuator.IJtActuator;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FreemarkerJtActuator implements IJtActuator {
    private static final String _lock ="";
    private static FreemarkerJtActuator _g;
    public static FreemarkerJtActuator singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new FreemarkerJtActuator();
                }
            }
        }

        return _g;
    }

    //引擎或配置
    Configuration _engine = new Configuration(Configuration.VERSION_2_3_28);
    //加载器
    StringTemplateLoaderEx _loader = new StringTemplateLoaderEx();

    private FreemarkerJtActuator() {
        _engine.setLocale(Locale.CHINESE);
        _engine.setNumberFormat("#");
        _engine.setDefaultEncoding("utf-8");
        _engine.setTemplateLoader(_loader);
        _engine.setCacheStorage(new MruCacheStorage(20,250));


        try {
            _engine.setSharedVaribles(XApp.global().shared());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        XApp.global().onSharedAdd((k,v)->{
            sharedSet(k,v);
        });
    }

    public void sharedSet(String name,Object obj){
        try {
            _engine.setSharedVariable(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }




    public boolean put(String name, AFileModel file){
        if(TextUtils.isEmpty(file.content)){
            return false;
        }else {
            _loader.putTemplate(name, file.content);
            return true;
        }
    }

    public Template get(String name) throws Exception{
        return _engine.getTemplate(name, "utf-8");
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "freemarker";
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
        _loader.removeTemplate(name);
    }

    @Override
    public void delAll(){
        _loader.removeTemplateAll();
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

            Template tmpl = _engine.getTemplate(name, "utf-8");

            StringWriter writer = new StringWriter();

            tmpl.process(model, writer);

            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
