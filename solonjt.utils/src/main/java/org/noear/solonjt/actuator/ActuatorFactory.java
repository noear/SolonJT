package org.noear.solonjt.actuator;

import org.noear.solon.core.XContext;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.ExceptionUtils;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solonjt.utils.ThreadUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 执行器工厂
 * */
public class ActuatorFactory {
    private static final Map<String, IJtActuator> _map = new HashMap<>();
    private static IJtActuator _def;
    private static IActuatorFactoryAdapter _adapter;

    public static void init(IActuatorFactoryAdapter adapter){
        _adapter = adapter;
    }

    /** 记录日志 */
    public static void errorLog(AFileModel file,String msg, Throwable err){
        _adapter.errorLog(file,msg,err);
    }

    /** 获取文件 */
    public static AFileModel fileGet(String path) throws Exception{
        return _adapter.fileGet(path);
    }

    /** 执行器清单 */
    public static Set<String> list(){
        return _map.keySet();
    }


    ///////////////////


    /**
     * 注册执行引擎
     */
    public static void register(IJtActuator engine) {
        if(engine.language().equals(_adapter.defaultActuator())){
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
        String text = (String) call(name, file, ctx, null, true);
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
     * 纯执行一个js文件（一般用于执行拦截器 或任务）
     */
    public static Object execOnly(AFileModel file, XContext ctx) throws Exception {
        IJtActuator tmp = _map.get(file.edit_mode);

        //最后是动态的
        if (tmp != null) {
            String path = file.path;
            String name = path.replace("/", "__");

            return tmp.exec(name, file, ctx, null, false);
        }

        return null;
    }

    /**
     * 执行一个文件并返回
     */
    public static Object call(String name, AFileModel file, XContext ctx, Map<String, Object> model, boolean outString) throws Exception {
        IJtActuator tmp = _map.get(file.edit_mode);

        try {
            if (tmp != null) {
                return tmp.exec(name, file, ctx, model, outString);
            } else {
                return _def.exec(name, file, ctx, model, outString);
            }
        } catch (Throwable err) {
            //如果出错，输出异常
            if (ctx != null && ctx.status() < 400) {
                String msg = ExceptionUtils.getString(err);
                if (ctx != null) {
                    ctx.output(msg);
                }

                errorLog(file, msg, err);
            }

            return null;
        }
    }
}
