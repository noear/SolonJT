package org.noear.thinkjt.extend.sited;

import org.noear.thinkjt.extend.sited.controller.ApiController;
import org.noear.thinkjt.extend.sited.dao.DbUtil;
import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.sharedGet("db", (DbContext db) -> {
            DbUtil.setDefDb(db);
        });

        app.sharedGet("cache", (ICacheServiceEx cache) -> {
            DbUtil.setDefCache(cache);
        });

        app.sharedAdd("eSiteD", new eSiteD());
        app.loadBean(ApiController.class);
    }
}
