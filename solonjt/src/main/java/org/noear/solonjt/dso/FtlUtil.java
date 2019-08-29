package org.noear.solonjt.dso;

import freemarker.cache.StringTemplateLoaderEx;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.XApp;

import java.io.StringWriter;
import java.util.Locale;

//后面可以添加新的引擎支持
public class FtlUtil {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
    StringTemplateLoaderEx tmls = new StringTemplateLoaderEx();

    private FtlUtil() {
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

    private static String lock="";
    private static FtlUtil _g;
    public static FtlUtil g(){
        if(_g == null){
            synchronized (lock){
                if(_g == null){
                    _g  =new FtlUtil();
                }
            }
        }

        return _g;
    }


    public boolean tryInit(String name, AFileModel file) {
        if (FtlUtil.g().has(name)) {
            return true;
        }

        return FtlUtil.g().put(name, file);
    }

    ////////
    public boolean has(String name){
        return tmls.findTemplateSource(name) != null;
    }

    public void del(String name){
        tmls.removeTemplate(name);
    }

    public void delAll(){
        tmls.removeTemplateAll();
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

    public String reander(String name, AFileModel file, Object model) throws Exception {
        if(tryInit(name,file)){
            Template tmpl = cfg.getTemplate(name, "utf-8");

            StringWriter writer = new StringWriter();

            tmpl.process(model, writer);

            return writer.toString();
        }else{
            return "";
        }
    }
}
