package org.noear.solonjt.dso;

import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.solonjt.Config;
import org.noear.solonjt.utils.Base64Utils;
import org.noear.solonjt.utils.HttpUtils;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.DataItem;
import org.noear.weed.DataList;
import org.noear.weed.DbContext;
import org.noear.weed.wrap.DbType;

import java.util.List;
import java.util.Map;

public class PluginUtil {
    private static DbContext db(){
        return DbUtil.db();
    }

    /**
     * 安装
     * */
    public static boolean install(String packageTag) {
        try {
            return installDo(packageTag, true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 重装
     * */
    public static  boolean reinstall(String packageTag) {
        try {
            return installDo(packageTag, false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean installDo(String packageTag, boolean onlyInstall) throws Exception{
        String center = XApp.cfg().get(Config.code_center);
        if (TextUtils.isEmpty(center)) {
            center = XApp.cfg().argx().get("center");
        }

        if (TextUtils.isEmpty(center)) {
            return false;
        }

        if(onlyInstall) {
            if (db().table("a_plugin").whereEq("plugin_tag", packageTag).exists()) {
                return false;
            }
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
            return false;
        }


        ONode body = data.get("data").get("body");
        ONode meta = data.get("data").get("meta");

        String plugin_tag = meta.get("plugin_tag").getString();
        String tag = plugin_tag.split("\\.")[0];


        String p_config = body.get("config").getString();
        String p_menu = body.get("menu").getString();
        String p_file = body.get("file").getString();
        String p_img = body.get("image").getString();

        ONode p_table = body.get("dbtable");


        //2.1.配置表
        if(TextUtils.isEmpty(p_config) ==false) {
            //获取需要排除的配置
            List<String> pcec = db().table("a_config").where("tag=? AND is_exclude=1",tag).select("name").getArray(0);

            //删掉不需要排除的配置
            db().table("a_config").where("tag=? AND is_exclude=0",tag).delete();

            String pcfg = Base64Utils.decode(p_config);
            List<Map<String, Object>> pcfg_d = ONode.deserialize(pcfg, List.class);

            for (Map<String, Object> m : pcfg_d) {
                String name = (String) m.get("name");
                Boolean is_modified = (Boolean) m.get("is_modified");

                if(pcec.contains(name) == false){
                    //插入不存在的
                    m.remove("cfg_id");
                    db().table("a_config").setMap(m).insert();
                }else if(is_modified == true) {
                    //
                    //如果用户可改的，同步编辑类型和提示
                    //
                    if (m.get("edit_type") == null) {
                        m.put("edit_type", "");
                    }

                    if (m.get("edit_placeholder") == null) {
                        m.put("edit_placeholder", "");
                    }

                    if (m.get("note") == null) {
                        m.put("note", "");
                    }

                    db().table("a_config").set("edit_type", m.get("edit_type"))
                            .set("edit_placeholder", m.get("edit_placeholder"))
                            .set("note", m.get("note"))
                            .whereEq("name", name)
                            .update();
                }

            }

            if (db().dbType() == DbType.H2) {
                db().table("a_config")
                        .set("value", "LocalJt")
                        .whereEq("name", "_frm_admin_title")
                        .update();
            }
        }

        //2.2.菜单表
        if(TextUtils.isEmpty(p_menu) == false) {
            List<String> pmec = db().table("a_menu").where("tag=? AND is_exclude=1", tag).select("concat(tag,label,url) name").getArray(0);

            db().table("a_menu").where("tag=? AND is_exclude=0", tag).delete();

            String pmenu = Base64Utils.decode(p_menu);
            List<Map<String, Object>> pmenu_d = ONode.deserialize(pmenu, List.class);

            if (pmenu_d != null) {
                for (Map<String, Object> m : pmenu_d) {
                    String pm_name = (m.get("tag") + "" + m.get("label") + m.get("url"));

                    if(pmec.contains(pm_name) == false) {
                        m.remove("menu_id");
                        db().table("a_menu").setMap(m).insert();
                    }
                }
            }
        }


        //2.3.文件表
        if(TextUtils.isEmpty(p_file) == false){

            List<String> pfec = db().table("a_file").where("tag=? AND is_exclude=1",tag).select("path").getArray(0);

            db().table("a_file").where("tag=? AND is_exclude=0",tag).delete();

            String pfile = Base64Utils.decode(p_file);
            List<Map<String, Object>> pfile_d = ONode.deserialize(pfile, List.class);

            if(pfile_d != null) {
                for (Map<String, Object> m : pfile_d) {
                    String path = (String) m.get("path");

                    if(pfec.contains(path) == false) {
                        m.remove("file_id");
                        if (m.get("content") != null) {
                            String c2 = Base64Utils.decode(m.get("content").toString());
                            m.put("content", c2);
                        }
                        db().table("a_file").setMap(m).insert();
                    }
                }
            }
        }


        //2.4.资源表
        if(TextUtils.isEmpty(p_img) == false){
            db().table("a_image").whereEq("tag",tag).delete();

            String pimg = Base64Utils.decode(p_img);
            List<Map<String, Object>> pimg_d = ONode.deserialize(pimg, List.class);

            if (pimg_d != null) {
                for (Map<String, Object> m : pimg_d) {
                    m.remove("img_id");
                    db().table("a_image").setMap(m).insert();
                }
            }
        }

        //3.业务表结构
        if(p_table.isObject()) {
            for (ONode n : p_table.obj().values()) {
                String v = n.getString();
                v = "CREATE TABLE IF NOT EXISTS " + Base64Utils.decode(v).substring(12);
                db().exe(v);
            }
        }

        //4.安装完成
        {
            db().table("a_plugin").set("is_installed", 0).whereEq("tag", tag).update();
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
        }

        //5.下载a_image表的jar包
        {
            int jar_num = 0;
            DataList pjar_d = db().table("a_image")
                    .whereEq("tag",tag)
                    .andEq("label","dep.jar")
                    .andEq("content_type","application/java-archive")
                    .select("path,data,data_md5,note")
                    .getDataList();

            for(DataItem m : pjar_d){
                String m_path = m.getString("path");
                String m_data = m.getString("data");
                String m_data_md5 = m.getString("data_md5");
                String m_note = m.getString("note");

                if(JtUtilEx.g2.loadJar(m_path, m_data, m_data_md5, m_note)){
                    jar_num++;
                }
            }
        }

        //重启(清空所有缓存)//不然勾子，可能会有缓存
        JtUtilEx.g2.restart();

        return true;
    }
}
