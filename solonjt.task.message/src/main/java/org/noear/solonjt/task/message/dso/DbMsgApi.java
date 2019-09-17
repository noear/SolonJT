package org.noear.solonjt.task.message.dso;

import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.task.message.Config;
import org.noear.solonjt.utils.ExceptionUtils;
import org.noear.weed.DbContext;

import java.sql.SQLException;
import java.util.List;

public class DbMsgApi {
    private static DbContext db(){
        return Config.db;
    }

    public static AFileModel fileGet(String path) throws Exception {
        return db().table("a_file")
                .where("`path`=?", path)
                .select("*")
                .getItem(AFileModel.class);
    }

    public static List<AFileModel> msgGetSubs(String topic) throws Exception {
        return db().table("a_file")
                .where("label=? AND is_disabled=0",topic)
                .select("file_id,tag,label,path,is_disabled")
                .getList(AFileModel.class);
    }

    public static void msgAdd(String topic, String content) throws Exception{
        db().table("a_message")
                .set("topic",topic)
                .set("content",content)
                .set("log_date","$DATE(NOW())")
                .set("log_fulltime","$NOW()")
                .insert();
    }

    public static AMessageModel msgGet(long msg_id) throws Exception{
        AMessageModel m = db().table("a_message")
                .where("msg_id=? AND state=0", msg_id)
                .select("*")
                .getItem(AMessageModel.class);

        if (m.state != 0) {
            return null;
        } else {
            return m;
        }
    }

    public static List<Long> msgGetList(int rows, int ntime) throws SQLException {
        return
                db().table("a_message")
                        .where("state=0 AND dist_ntime<?",ntime)
                        .orderBy("msg_id ASC")
                        .limit(rows)
                        .select("msg_id")
                        .getArray("msg_id");
    }


    public static boolean msgSetState(long msg_id, int state){
        return msgSetState(msg_id,state, 0);
    }

    public static boolean msgSetState(long msg_id, int state, int nexttime) {
        try {
            db().table("a_message")
                    .set("state", state).expre(tb -> {
                if (state == 0) {
                    int ntime = DisttimeUtil.nextTime(1);
                    tb.set("dist_ntime", ntime);
                    //可以检查处理中时间是否过长了？可手动恢复状态
                }

                if (nexttime > 0) {
                    tb.set("dist_ntime", nexttime);
                }
            })
                    .where("msg_id=? AND (state=0 OR state=1)", msg_id)
                    .update();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();

            LogUtil.log("msg", "setMessageState", msg_id + "", 0, "", ExceptionUtils.getString(ex));

            return false;
        }
    }

    //设置消息重试状态（过几秒后再派发）
    public static boolean msgSetRepet(AMessageModel msg, int state)  {
        try {
            msg.dist_count += 1;

            int ntime = DisttimeUtil.nextTime(msg.dist_count);

            db().table("a_message").usingExpr(true)
                    .set("state", state)
                    .set("dist_ntime", ntime)
                    .set("dist_count", "$dist_count+1")
                    .where("msg_id=? AND (state=0 OR state=1)", msg.msg_id)
                    .update();

            return true;
        }catch (SQLException ex){
            ex.printStackTrace();

            LogUtil.log("msg","setMessageRepet",msg.msg_id+"",0,"",ExceptionUtils.getString(ex));

            return false;
        }
    }

    public static void msgAddDistribution(long msg_id, AFileModel subs) throws SQLException {


        boolean isExists = db().table("a_message_distribution")
                .where("msg_id=?", msg_id).and("file_id=?", subs.file_id)
                .exists();

        if (isExists == false) {
            db().table("a_message_distribution").usingExpr(true)
                    .set("msg_id", msg_id)
                    .set("file_id", subs.file_id)
                    .set("receive_url", subs.path)
                    .set("receive_way", 0)
                    .set("log_date", "$DATE(NOW())")
                    .set("log_fulltime", "$NOW()")
                    .insert();
        }
    }

    public static List<AMessageDistributionModel> msgGetDistributionList(long msg_id) throws  Exception{
        return db().table("a_message_distribution")
                .where("msg_id=? AND (state=0 OR state=1)", msg_id)
                .select("*")
                .getList(AMessageDistributionModel.class);
    }

    //设置派发状态（成功与否）
    public static boolean msgSetDistributionState(long msg_id, AMessageDistributionModel dist, int state) {
        try {
            db().table("a_message_distribution")
                    .set("state", state)
                    .set("duration",dist._duration)
                    .where("msg_id=? and file_id=? and state<>2",msg_id, dist.file_id)
                    .update();

            return true;
        }catch (Exception ex){
            ex.printStackTrace();

            LogUtil.log("msg","setDistributionState",msg_id+"", 0, "",ExceptionUtils.getString(ex));

            return false;
        }
    }
}
