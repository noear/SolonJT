package org.noear.solonjt.engine.freemarker;

import freemarker.cache.StringTemplateLoaderEx;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.solon.XApp;
import org.noear.solon.core.XContext;
import org.noear.solonjt.engine.IJtEngine;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FreemarkerJtEngine implements IJtEngine{
    private static final String _lock ="";
    private static FreemarkerJtEngine _g;
    public static FreemarkerJtEngine singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new FreemarkerJtEngine();
                }
            }
        }

        return _g;
    }

    /** 配置对象 */
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
    /** 加载器对象 */
    StringTemplateLoaderEx tmls = new StringTemplateLoaderEx();

    private FreemarkerJtEngine() {
        cfg.setLocale(Locale.CHINESE);
        cfg.setNumberFormat("#");
        cfg.setDefaultEncoding("utf-8");
        cfg.setTemplateLoader(tmls);

        try {
            cfg.setSharedVaribles(XApp.global().shared());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        XApp.global().onSharedAdd((k,v)->{
            sharedSet(k,v);
        });
    }

    public void sharedSet(String name,Object obj){
        try {
            cfg.setSharedVariable(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }




    public boolean put(String name, AFileModel file){
        if(TextUtils.isEmpty(file.content)){
            return false;
        }else {
            tmls.putTemplate(name, file.content);
            return true;
        }
    }

    public Template get(String name) throws Exception{
        return cfg.getTemplate(name, "utf-8");
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
        return tmls.findTemplateSource(name) != null;
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
        tmls.removeTemplate(name);
    }

    @Override
    public void delAll(){
        tmls.removeTemplateAll();
    }

    @Override
    public Object exec(String name, AFileModel file, XContext ctx, Map<String,Object> model, boolean asRaw) throws Exception {
        if(preLoad(name,file)){
            if(model == null){
                model = new HashMap<>();
            }

            if(ctx == null){
                model.put("ctx", XContext.current());
            }else{
                model.put("ctx", ctx);
            }

            Template tmpl = cfg.getTemplate(name, "utf-8");

            StringWriter writer = new StringWriter();

            tmpl.process(model, writer);

            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
