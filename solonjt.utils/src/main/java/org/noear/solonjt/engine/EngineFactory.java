package org.noear.solonjt.engine;

import org.noear.solon.core.XContext;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.ExceptionUtils;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solonjt.utils.ThreadUtils;
import org.noear.solonjt.utils.lambda.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用执行工具
 * */
public class EngineFactory {
    private static final Map<String, IJtEngine> _map = new HashMap<>();
    private static IJtEngine _def;
    private static Act2<AFileModel,String> _logHandler;
    private static Fun1Ex<AFileModel,String> _fileHandler;

    public static void init(Act2<AFileModel,String> logHandler, Fun1Ex<AFileModel,String> fileHandler){
        _logHandler = logHandler;
        _fileHandler = fileHandler;
    }

    //记录日志
    public static void log(AFileModel file,String err){
        _logHandler.run(file,err);
    }

    //获取文件
    public static AFileModel fileGet(String path) throws Exception{
        return _fileHandler.run(path);
    }



    /**
     * 注册执行引擎
     */
    public static void register(IJtEngine engine, boolean isDef) {
        if(isDef){
            _def =engine;
        }

        _map.put(engine.language(), engine);
    }

    /**
     * 删除代码缓存
     */
    public static void del(String name) {
        _map.forEach((k, v) -> {
            v.del(name);
        });
    }

    /**
     * 删除所有代码缓存
     */
    public static void delAll() {
        _map.forEach((k, v) -> {
            v.delAll();
        });
    }


    /**
     * 执行一个文件并输出（jsx 或 ftl）
     */
    public static void exec(String name, AFileModel file, XContext ctx) throws Exception {
        //最后是动态的
        String text = (String) call(name, file, ctx, null, false);
        String call = ctx.param("callback");

        if (ctx.status() == 404) {
            return;
        }

        ctx.charset("utf-8");


        if (TextUtils.isEmpty(file.content_type) == false) {
            ctx.contentType(file.content_type);
        }

        if (text != null) {
            if (TextUtils.isEmpty(call) == false) {
                /**
                 * jsonp 的请求支持
                 * */
                StringBuilder sb = ThreadUtils.getStringBuilder();
                sb.append(call).append("(").append(text).append(");");
                text = sb.toString();

                ctx.contentType("application/x-javascript");
            } else {
                if (TextUtils.isEmpty(file.content_type)) {
                    /**
                     * 如果没有预设content type；则自动检测
                     * */
                    if (text.startsWith("<")) {
                        ctx.contentType("text/html");
                    }

                    if (text.startsWith("{")) {
                        ctx.contentType("application/json");
                    }
                }
            }
        }

        if (text != null) {
            ctx.output(text);
        }
    }

    /**
     * 执行一个文件并返回
     */
    public static Object call(String name, AFileModel file, XContext ctx, Map<String, Object> model, boolean asRaw) throws Exception {
        IJtEngine tmp = _map.get(file.edit_mode);

        try {
            if (tmp != null) {
                return tmp.exec(name, file, ctx, model, asRaw);
            } else {
                return _def.exec(name, file, ctx, model, asRaw);
            }
        } catch (Exception ex) {
            //如果出错，输出异常
            if (ctx != null && ctx.status() < 400) {
                String err = ExceptionUtils.getString(ex);
                if (ctx != null) {
                    ctx.output(err);
                }

                log(file, err);
            }

            return null;
        }
    }

    /**
     * 纯执行一个js文件（不返回；一般用于执行拉截器）
     */
    public static void execOnly(AFileModel file, XContext ctx) throws Exception {
        IJtEngine tmp = _map.get(file.edit_mode);

        //最后是动态的
        if (tmp != null) {
            String path = file.path;
            String name = path.replace("/", "__");

            tmp.exec(name, file, ctx, null, true);
        }
    }
}
