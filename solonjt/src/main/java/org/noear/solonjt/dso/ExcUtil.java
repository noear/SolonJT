package org.noear.solonjt.dso;

import org.noear.solonjt.utils.ExceptionUtils;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.core.XContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用执行工具
 * */
public class ExcUtil {
    /**
     * 执行一个文件并输出（jsx 或 ftl）
     * */
    public static void  exec(String name,AFileModel file, XContext ctx) throws Exception {
        //最后是动态的
        String text = (String) call(name, file, ctx, true);
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
                StringBuilder sb = new StringBuilder();
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
     * */
    public static Object call(String name,AFileModel file, XContext ctx, boolean asApi) throws Exception {

        try {
            if (file.edit_mode.equals("javascript")) {
                /**
                 * 用javascript引擎执行
                 * */
                Object tmp = JsxUtil.g().runApi(name, file, asApi);

                if (tmp == null) {
                    return null;
                } else {
                    if (asApi) {
                        return tmp.toString();
                    } else {
                        return tmp;
                    }
                }

            } else {
                /**
                 * 用freemark引擎执行
                 * */
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                return FtlUtil.g().reander(name, file, model).trim();
            }
        } catch (Exception ex) {
            //如果出错，输出异常
            if(ctx.status() < 400) {
                String err = ExceptionUtils.getString(ex);
                if (ctx != null) {
                    ctx.output(err);
                }

                LogUtil.log("_file", file.tag, file.path, 0, "", err);
            }

            return null;
        }
    }

    /**
     * 纯执行一个js文件（不返回；一般用于执行拉截器）
     * */
    public static void  execJsOnly(AFileModel file) throws Exception {
        //最后是动态的
        if (file.edit_mode.equals("javascript")) {
            String path = file.path;
            String name = path.replace("/", "__");

            JsxUtil.g().runApi(name, file, true);
        }
    }
}
