package org.noear.solonjt.task.message.controller;

import org.noear.solonjt.actuator.ActuatorFactory;
import org.noear.solonjt.actuator.IJtTask;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.task.message.dso.*;
import org.noear.solonjt.utils.ExceptionUtils;
import org.noear.solonjt.utils.Timespan;
import org.noear.weed.ext.Act3;

import java.util.List;

import static java.lang.System.out;

public class MessageTask implements IJtTask {
    @Override
    public String getName() {
        return "_message";
    }

    int _interval = 500;

    @Override
    public int getInterval() {
        return _interval;
    }

    private int rows = 100;

    @Override
    public void exec() throws Exception {

        int ntime = DisttimeUtil.currTime();
        List<Long> msgList = DbMsgApi.msgGetList(rows, ntime);

        for (Long msgID : msgList) {

            AMessageModel msg = DbMsgApi.msgGet(msgID);
            if (msg == null) {
                continue;
            }

            //置为处理中
            DbMsgApi.msgSetState(msgID, 1);

            try {
                distribute(msg);


            } catch (Exception ex) {
                ex.printStackTrace();

                DbMsgApi.msgSetRepet(msg, 0); //如果失败，重新设为0 //重新操作一次

                LogUtil.log(getName(), "distribute", msg.topic, msg.msg_id + "", 0, "", ExceptionUtils.getString(ex));
            }
        }

        if (msgList.size() == 0) {
            _interval = 1000 * 2;
        } else {
            _interval = 500;
        }
    }

    private void distribute(AMessageModel msg) throws Exception {
        //1.取出订阅者
        List<AFileModel> subsList = DbMsgApi.msgGetSubs(msg.topic);

        //1.2.如果没有订阅者，就收工
        if (subsList.size() == 0) {
            DbMsgApi.msgSetState(msg.msg_id, -2, 0);
            return;
        }

        //2.尝试建立分发关系
        for (AFileModel m : subsList) {
            DbMsgApi.msgAddDistribution(msg.msg_id, m);
        }

        //3.获出待分发任务
        List<AMessageDistributionModel> distList = DbMsgApi.msgGetDistributionList(msg.msg_id);

        //3.2.如果没有可派发对象，就收工
        if (distList.size() == 0) {
            DbMsgApi.msgSetState(msg.msg_id, 2);
            return;
        }

        //4.开始派发
        //
        StateTag state = new StateTag();
        state.msg = msg;
        state.total = distList.size();

        for (AMessageDistributionModel m : distList) {
            m._start_time = System.currentTimeMillis();

            distributeMessage(state, msg, m, distributeMessage_callback);
        }
    }

    private Act3<StateTag, AMessageDistributionModel, Boolean> distributeMessage_callback = (tag, dist, isOk) -> {
        tag.count += 1;
        if (isOk) {
            if (DbMsgApi.msgSetDistributionState(tag.msg.msg_id, dist, 2)) {
                tag.value += 1;
            }
        } else {
            DbMsgApi.msgSetDistributionState(tag.msg.msg_id, dist, 1);
        }

        //4.返回派发结果
        if (tag.count == tag.total) {
            if (tag.value == tag.total) {
                DbMsgApi.msgSetState(dist.msg_id, 2);

                if (tag.msg.dist_count >= 4) {
                    out.print("发送短信报警---\r\n");
                }
            } else {
                DbMsgApi.msgSetRepet(tag.msg, 0);

                if (tag.msg.dist_count >= 4) {
                    out.print("发送短信报警---\r\n");
                }

            }
        }
    };

    private void distributeMessage_log(AMessageModel msg, AMessageDistributionModel dist, String note){
        LogUtil.log(getName(), "distributeMessage", msg.msg_id + "", dist.file_id + "", 0, dist.receive_url, note);
    }

    private void distributeMessage(StateTag tag, AMessageModel msg, AMessageDistributionModel dist, Act3<StateTag, AMessageDistributionModel, Boolean> callback) {

        try {
            AFileModel task = DbMsgApi.fileGet(dist.receive_url);

            if (dist.receive_way == 0) {
                new Thread(() -> {
                    try {
                        Object tmp = ActuatorFactory.execOnly(task, null);
                        dist._duration = new Timespan(System.currentTimeMillis(), dist._start_time).seconds();

                        if(tmp == null || tmp.toString().equals("OK")){
                            distributeMessage_log(msg,dist,"OK");
                            callback.run(tag, dist, true);
                        }else{
                            distributeMessage_log(msg,dist,(tmp==null?"null" : tmp.toString()));
                            callback.run(tag, dist, false);
                        }
                    } catch (Exception ex) {
                        distributeMessage_log(msg,dist,ExceptionUtils.getString(ex));
                        callback.run(tag, dist, false);
                    }

                }).start();
            }

        } catch (Exception ex) {
            distributeMessage_log(msg,dist,ExceptionUtils.getString(ex));
            callback.run(tag, dist, false);
        }
    }
}
