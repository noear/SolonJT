package org.noear.solonjt.event.http;

import org.noear.solonjt.Config;
import org.noear.solonjt.dso.*;
import org.noear.solonjt.executor.ExecutorFactory;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

import java.util.HashMap;
import java.util.List;

/**
 * 文件后缀拦截器的代理（数据库安全）
 * */
public class SufHandler implements XHandler {
    private static final String _lock = "";
    private static  SufHandler _g = null;
    public static SufHandler g(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new SufHandler();
                }
            }
        }
        return  _g;
    }



    private SufHandler(){
        reset();
    }

    @Override
    public void handle(XContext ctx) throws Exception {
        String path = ctx.path();
        for (String suf : _cacheMap.keySet()) {
            if (path.endsWith(suf)) {
                ctx.setHandled(true);

                exec(ctx, _cacheMap.get(suf));
                return;
            }
        }
    }

    private void exec(XContext ctx, String path) throws Exception {
        String path2 = path;//AFileUtil.path2(path);//不需要转为*
        String name = path2.replace("/", "__");

        AFileModel file = AFileUtil.get(path2);

        //文件不存在，则404
        if (file.file_id == 0) {
            ctx.status(404);
            return;
        }

        //不支持后缀代理，跳过
        if (Config.filter_file.equals(file.label) == false) {
            return;
        }

        ExecutorFactory.exec(name,file,ctx);
    }



    private HashMap<String,String> _cacheMap = new HashMap<>();
    public void del(String note) {
        if (TextUtils.isEmpty(note)) {
            return;
        }

        String suf = note.split("#")[0];
        if (suf.length()>0) {
            if (suf.startsWith(".")) {
                _cacheMap.remove(suf);
            } else {
                _cacheMap.remove("." + suf);
            }
        }
    }

    public void add(String path, String note){
        if(TextUtils.isEmpty(note)){
            return;
        }

        String suf = note.split("#")[0];

        if (suf.length()>0) {
            if (suf.startsWith(".")) {
                _cacheMap.put(suf, path);
            } else {
                _cacheMap.put("." + suf, path);
            }
        }
    }

    public void reset() {
        if (DbUtil.db() == null) {
            return;
        }

        try {
            _cacheMap.clear();

            List<AFileModel> list = DbApi.fileFilters();
            for (AFileModel c : list) {
                if(TextUtils.isEmpty(c.note)){
                    continue;
                }

                String suf = c.note.split("#")[0];

                if (suf.length()>0) {
                    if (suf.startsWith(".")) {
                        _cacheMap.put(suf, c.path);
                    } else {
                        _cacheMap.put("." + suf, c.path);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
