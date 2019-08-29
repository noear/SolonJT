package org.noear.solonjt.dso;

import org.noear.solonjt.Config;
import org.noear.solonjt.utils.Datetime;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.core.XContext;
import org.noear.weed.DbContext;
import org.noear.weed.DbTableQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 引擎基础的数据库处理接口
 * */
public class DbApi {
    private static DbContext db(){
        return DbUtil.db();
    }

    /** 新建文件 */
    public static boolean fileNew(int fid, XContext ctx) throws Exception {
        DbTableQuery qr = db().table("a_file")
                .set("path", ctx.param("path", ""))
                .set("tag", ctx.param("tag", ""))
                .set("is_staticize", ctx.paramAsInt("is_staticize", 0))
                .set("is_editable", ctx.paramAsInt("is_editable", 0))
                .set("link_to", ctx.param("link_to", ""))
                .set("edit_mode", ctx.param("edit_mode", ""))
                .set("content_type", ctx.param("content_type", ""));

        if (fid > 0) {
            return qr.where("file_id=?", fid)
                    .update() > 0;
        } else {
            return qr.insert() > 0;
        }
    }

    public static AFileModel fileGet(String path) throws Exception {
        return db().table("a_file")
                .where("`path`=?", path)
                .select("*")
                .getItem(AFileModel.class);
    }

    public static List<AFileModel> fileGetPaths(String tag,String label, boolean isCache) throws Exception {
        if(TextUtils.isEmpty(tag) && TextUtils.isEmpty(label)){
            return new ArrayList<>();
        }

        return db().table("a_file").where("1=1").expre((tb) -> {
                    if (TextUtils.isEmpty(tag) == false) {
                        tb.and("`tag`=?", tag);
                    }

                    if (TextUtils.isEmpty(label) == false) {
                        tb.and("`label`=?", label);
                    }
                })
                .select("path,note")
                .caching(DbUtil.cache)
                .usingCache(isCache)
                .getList(AFileModel.class);
    }

    public static List<String> fileGetPathAll() throws Exception {
        return db().table("a_file").select("path")
                .getDataList().toArray(0);
    }


    public static boolean fileSet(int fid, String fcontent) throws Exception {
        if(fid<1){
            return false;
        }

        if(fcontent == null){
            return false;
        }

        AFileModel fm = DbUtil.db().table("a_file")
                .where("file_id=?", fid)
                .select("*")
                .getItem(AFileModel.class);


        if(fm.is_editable==false){
            return false;
        }

        DbUtil.db().table("a_file")
                .set("content", fcontent)
                .set("update_fulltime", "$NOW()")
                .where("file_id=?", fid)
                .update();

        String path2 = fm.path;
        String name = path2.replace("/", "__");

        AFileUtil.remove(path2);
        FtlUtil.g().del(name);
        JsxUtil.g().del(name);

        return true;
    }

    public static List<AFileModel> fileFilters() throws Exception{
        return
        DbUtil.db().table("a_file")
                .where("`label` = ?", Config.filter_file)
                .select("path,note")
                .getList(AFileModel.class);

    }

    public static List<AFileModel> pathFilters() throws Exception{
        return
                DbUtil.db().table("a_file")
                        .where("`label` = ?",Config.filter_path)
                        .select("path,note")
                        .getList(AFileModel.class);

    }


    public static String cfgGet(String name) {
        return cfgGet(name,"");
    }

    public static String cfgGet(String name, String def) {
        try {
            return db().table("a_config")
                    .where("`name`=?", name)
                    .select("value")
                    .caching(DbUtil.cache)
                    .cacheTag("cfg_" + name)
                    .getValue(def);
        }catch (Exception ex){
            return def;
        }
    }

    public static boolean cfgSet(String name, String value, String label) throws Exception{
        boolean is_ok =false;
        if(db().table("a_config") .where("`name`=?",name).exists()){
            is_ok = db().table("a_config")
                    .set("value",value)
                    .expre((tb)->{
                        if(label!=null){
                            tb.set("label",label);
                        }
                    })
                    .set("update_fulltime","$NOW()")
                    .where("`name`=?",name)
                    .update()>0;
        }else{
            is_ok = db().table("a_config")
                    .set("name",name)
                    .set("value",value)
                    .expre((tb)->{
                        if(label!=null){
                            tb.set("label",label);
                        }
                    })
                    .insert() > 0;
        }

        DbUtil.cache.clear("cfg_"+name);

        return is_ok;
    }

    public static List<Map<String,Object>> menuGet(String label,int pid) throws SQLException {
        return db().table("a_menu")
                .where("`label`=? AND pid=? AND is_disabled=0", label, pid)
                .orderBy("order_number ASC")
                .select("*")
                .caching(DbUtil.cache)
                .cacheTag("menu_" + label)
                .getMapList();
    }

    public static AImageModel imgGet(String path) throws Exception{
        return db().table("a_image")
                .where("`path`=?",path)
                .select("*")
                .caching(DbUtil.cache)
                .getItem(AImageModel.class);
    }

    public static boolean imgSet(String tag,String path,String content_type, String data,String label) throws Exception{
        boolean is_ok =false;

        DbTableQuery qr = db().table("a_image")
                .set("`content_type`",content_type)
                .set("`data`",data)
                .set("`data_size`",data.length())
                .set("`label`",label)
                .set("update_fulltime","$NOW()");

        if(tag!=null){
            qr.set("`tag`",tag);
        }

        if(db().table("a_image") .where("`path`=?",path).exists()){
            is_ok = qr.where("`path`=?",path)
                    .update()>0;

            AImageUtil.remove(path);
        }else {
            is_ok = qr.set("`path`", path)
                    .insert() > 0;
        }

        return is_ok;
    }

    public static boolean imgUpd(String path, String data) throws Exception {
        boolean is_ok = db().table("a_image")
                .set("`data`",data)
                .where("`path`=?",path)
                .update()>0;

        return is_ok;
    }

    public static void taskResetState() throws SQLException {
        db().table("a_file")
                .set("plan_state", 9)
                .where("plan_state=?", 2)
                .update();
    }

    public static List<AFileModel> taskGetList() throws Exception {
        return db().table("a_file")
                        .where("label=? AND is_disabled=0","task.plan")
                        .select("*")
                        .getList(AFileModel.class);
    }

    public static void taskSetState(AFileModel task, int state) throws SQLException {

        //对以天为进阶的任务，做同时间处理
        if (task._is_day_task && state == 9) {
            String s_d = new Datetime(task.plan_last_time).toString("yyyy-MM-dd");
            String s_t = new Datetime(task.plan_begin_time).toString("HH:mm:ss");

            try {
                Datetime temp = Datetime.parse(s_d + " " + s_t, "yyyy-MM-dd HH:mm:ss");
                task.plan_last_time = temp.getFulltime();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        db().table("a_file")
                .set("plan_state", state)
                .set("plan_count", task.plan_count)
                .set("plan_last_time", task.plan_last_time)
                .expre((tb) -> {
                    if (task.plan_last_timespan > 0) {
                        tb.set("plan_last_timespan", task.plan_last_timespan);
                    }
                })
                .where("file_id=?", task.file_id)
                .update();
    }

    public static boolean log(Map<String,Object> data)  {
        Map<String,Object> map = new HashMap<>();


        map.put("tag", data.get("tag"));
        map.put("tag1", data.get("tag1"));
        map.put("tag2", data.get("tag2"));
        map.put("tag3", data.get("tag3"));
        map.put("tag4", data.get("tag4"));
        map.put("summary", data.get("summary"));
        map.put("content", data.get("content"));

        map.put("level", data.get("level"));
        map.put("log_date",Datetime.Now().getDate());
        map.put("log_fulltime","$NOW()");

        return do_log(map);
    }

    private static boolean do_log(Map<String,Object> map){

        XFun fun = XUtil.g.xfunGet("log");

        try {
            if(fun!=null){
                fun.run(map);
            }else {
                db().table("a_log")
                        .setMap(map)
                        .insert();
            }

            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

}
