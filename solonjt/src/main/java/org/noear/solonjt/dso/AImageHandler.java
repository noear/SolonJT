package org.noear.solonjt.dso;

import org.noear.solonjt.model.AImageModel;
import org.noear.solonjt.utils.Base64Utils;
import org.noear.solon.core.XContext;

import java.util.Date;

/**
 * 资源文件代理（如：图片，视频，音频等资源）
 * */
public class AImageHandler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";


    public static void handle(XContext context, AImageModel file) throws Exception {
        String path = context.path();

        context.setHandled(true);

        String modified_since = context.header("If-Modified-Since");
        String modified_now = app_runtime.toString();

        if (modified_since != null) {
            if (modified_since.equals(modified_now)) {
                context.headerSet(CACHE_CONTROL, "max-age=6000");
                context.headerSet(LAST_MODIFIED, modified_now);
                context.contentType(file.content_type);
                context.charset("utf-8");
                context.status(304);
                return;
            }
        }

        int idx = path.lastIndexOf(".");
        if (idx > 0) {
            String mime = file.content_type;

            if (mime != null) {
                context.headerSet(CACHE_CONTROL, "max-age=6000");
                context.headerSet(LAST_MODIFIED, app_runtime.toString());
                context.contentType(file.content_type);
                context.charset("utf-8");
            }
        }

        byte[] data = Base64Utils.decodeByte(file.data);

        context.status(200);
        context.outputStream().write(data);
        context.outputStream().flush();

    }

    private static final Date app_runtime = new Date();
}
