package org.noear.solonjt.event.http;

import org.noear.solonjt.dso.*;
import org.noear.solonjt.executor.ExecutorFactory;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 应用文件的代理，静态文件或动态文件（数据库安全）
 * */
public class AppHandler implements XHandler {

    private static final String _lock = "";
    private static  AppHandler _g = null;
    public static AppHandler g(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new AppHandler();
                }
            }
        }
        return  _g;
    }


    @Override
    public void handle(XContext ctx) throws Exception {
        try {
            do_handle(ctx.path(), ctx);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void do_handle(String path,XContext ctx) throws Exception {
        String path2 = AFileUtil.path2(path);
        String name = null;

        AFileModel file = null;

        //::先用路由工具做检测，防止数据库被恶意刷暴
        if (RouteHelper.has(path)) {
            file = AFileUtil.get(path);
            name = path.replace("/", "__");
        } else if (RouteHelper.has(path2)) {
            file = AFileUtil.get(path2);
            name = path2.replace("/", "__");
        }

        //文件不存在，则404
        if (file == null || file.file_id == 0) {
            ctx.status(404);
            return;
        }

        if (file.is_disabled) {
            ctx.status(403);
            return;
        }

        if(file.content_type != null && file.content_type.startsWith("code/")){
            ctx.status(403);
            return;
        }

        //如果有跳转，则跳转
        if (TextUtils.isEmpty(file.link_to) == false) {
            if (file.link_to.startsWith("@")) {
                do_handle(file.link_to.substring(1), ctx);
            } else {
                ctx.redirect(file.link_to);
            }
            return;
        }

        //如果是静态
        if (file.is_staticize) {
            if (file.content == null) {
                ctx.status(404);
            } else {
                AFileStaticHandler.handle(ctx, path, file);
            }
            return;
        }

        ExecutorFactory.exec(name, file, ctx);
    }
}
