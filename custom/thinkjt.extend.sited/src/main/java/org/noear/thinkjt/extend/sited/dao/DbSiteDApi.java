package org.noear.thinkjt.extend.sited.dao;

import org.noear.thinkjt.extend.sited.dao.custom.DdSource;
import org.noear.thinkjt.extend.sited.utils.TextUtils;
import org.noear.weed.DataItem;
import org.noear.weed.DbContext;
import org.noear.weed.IDataItem;

public class DbSiteDApi {

    public static void saveSiteD(int puid, DdSource source, String path) throws Exception{
        DbContext db = DbUtil.db();

        boolean exists = db.table("sited").where("guid=?", source.guid).exists();
        int dtype = source.main.dtype();
        int btype = source.main.btype();
        if (btype == 0) btype = dtype;

        int is_private = (source.isPrivate()?1:0);


        if (exists) {
            IDataItem d = new DataItem().set("client_ver", source.engine)
                    .set("ver", source.ver)
                    .set("author", source.author)
                    .set("title", source.title)
                    .set("exp", source.expr)
                    .set("url", source.url)
                    .set("file",path)
                    .set("is_ok", 1)
                    .set("is_vip", source.meta.attrs.getInt("vip", 0))
                    .set("is_private",is_private)
                    .set("intro", source.intro)
                    .set("dtype", dtype)
                    .set("btype", btype)
                    .set("update_time", "$NOW()");

            if (TextUtils.isEmpty(source.logo) == false) {
                d.set("logo", source.logo);
            }

            db.table("sited").where("guid=?", source.guid).update(d);


        }
        else {
            IDataItem d = new DataItem()
                    .set("guid", source.guid)
                    .set("puid", puid)
                    .set("client_ver", source.engine)
                    .set("ver", source.ver)
                    .set("author", source.author)
                    .set("title", source.title)
                    .set("is_ok", 1)
                    .set("is_vip", source.meta.attrs.getInt("vip", 0))
                    .set("exp", source.expr)
                    .set("url", source.url)
                    .set("intro", source.intro)
                    .set("file", path)
                    .set("dtype", dtype)
                    .set("btype", btype)
                    .set("update_time", "$NOW()");

            if (TextUtils.isEmpty(source.logo) == false) {
                d.set("logo", source.logo);
            }

            db.table("sited").insert(d);
        }
    }
}
