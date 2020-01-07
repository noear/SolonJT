package org.noear.solonjt.dso;

import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.solonjt.Config;
import org.noear.solonjt.utils.Base64Utils;
import org.noear.solonjt.utils.HttpUtils;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.DbContext;
import org.noear.weed.wrap.DbType;

import java.util.List;
import java.util.Map;

public class PluginUtil {
    private static DbContext db(){
        return DbUtil.db();
    }

    public static void setup(String packageTag) throws Exception{
        String center = XApp.cfg().get(Config.code_center);
        if (TextUtils.isEmpty(center)) {
            center = XApp.cfg().argx().get("center");
        }

        if (TextUtils.isEmpty(center)) {
            return;
        }


        String url = null;

        if(center.indexOf("://")>0){
            url =  center + "/.plugin/pull.jsx?plugin_tag=" + packageTag;
        }else {
            url = "http://" + center + "/.plugin/pull.jsx?plugin_tag=" + packageTag;
        }


        String json = new HttpUtils(url).get();
        ONode data = ONode.load(json);
        if (data.get("code").getInt() != 1) {
            return;
        }

        ONode body = data.get("data").get("body");
        ONode meta = data.get("data").get("meta");

        String j_config = Base64Utils.decode(body.get("config").getString());
        String j_file = Base64Utils.decode(body.get("file").getString());
        String j_img = Base64Utils.decode(body.get("image").getString());
        String j_menu = Base64Utils.decode(body.get("menu").getString());

        //2.1.配置表
        List<Map<String, Object>> d_config = ONode.deserialize(j_config, List.class);
        for (Map<String, Object> m : d_config) {
            m.remove("cfg_id");
            db().table("a_config").setMap(m).insert();
        }

        if(db().dbType() == DbType.H2){
            db().table("a_config")
                    .set("value","LocalJt")
                    .whereEq("name","_frm_admin_title")
                    .update();
        }

        //2.2.文件表
        List<Map<String, Object>> d_file = ONode.deserialize(j_file, List.class);
        for (Map<String, Object> m : d_file) {
            m.remove("file_id");
            if (m.get("content") != null) {
                String c2 = Base64Utils.decode(m.get("content").toString());
                m.put("content", c2);
            }
            db().table("a_file").setMap(m).insert();
        }

        //2.3.资源表
        List<Map<String, Object>> d_img = ONode.deserialize(j_img, List.class);
        if (d_img != null) {
            for (Map<String, Object> m : d_img) {
                m.remove("img_id");
                db().table("a_image").setMap(m).insert();
            }
        }

        List<Map<String, Object>> d_menu = ONode.deserialize(j_menu, List.class);
        if (d_menu != null) {
            for (Map<String, Object> m : d_menu) {
                m.remove("menu_id");
                db().table("a_menu").setMap(m).insert();
            }
        }

        db().table("a_plugin")
                .set("plugin_tag", meta.get("plugin_tag").getString())
                .set("tag", meta.get("tag").getString())
                .set("name", meta.get("name").getString())
                .set("author", meta.get("author").getString())
                .set("contacts", meta.get("contacts").getString())
                .set("ver_name", meta.get("ver_name").getString())
                .set("ver_code", meta.get("ver_code").getString())
                .set("description", meta.get("description").getString())
                .set("thumbnail", meta.get("thumbnail").getString())
                .set("is_installed", 1)
                .upsert("plugin_tag");


        db().table("a_config")
                .set("value", "Iv1H81dI2ZNzDS2n")
                .where("name=?", "_frm_admin_pwd")
                .update();

        db().table("a_config")
                .set("value", "0")
                .where("name=?", "_frm_enable_dev")
                .update();

        db().table("a_file")
                .set("link_to", "")
                .where("path='/'")
                .update();
    }
}
