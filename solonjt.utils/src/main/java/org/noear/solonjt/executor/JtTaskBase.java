package org.noear.solonjt.executor;

import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.solonjt.dso.CfgUtil;
import org.noear.solonjt.utils.TextUtils;

import java.util.Map;
import java.util.Objects;

public abstract class JtTaskBase implements IJtTask{
    protected JtTaskBase(String name, int interval){
        _name = name;
        _interval = interval;
        _interval_bak = interval;
    }
    protected String _name;
    protected int _interval;
    protected int _interval_bak;//各份

    private String _node_id;
    protected String node_id(){
        if(_node_id == null){
            _node_id = XApp.global().prop().argx().get("node");
        }

        return _node_id;
    }

    private ONode _node_cfg = null;
    private int __node_cfg_hash = 0;

    protected ONode node_cfg() {
        if (TextUtils.isEmpty(node_id()) == false) {

            try {
                Map<String, Object> tmp = CfgUtil.cfgGet(node_id());

                if (tmp != null && tmp.get("value") != null) {

                    String cfg_str = tmp.get("value").toString().trim();

                    int hash = Objects.hashCode(cfg_str);

                    //如果没变化，不重新生成配置
                    if (__node_cfg_hash != hash) {
                        __node_cfg_hash = hash;

                        if(cfg_str.startsWith("{")) {
                            _node_cfg = ONode.fromStr(cfg_str);
                        }else{
                            _node_cfg = null;
                        }
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return _node_cfg;
    }

    protected boolean node_current_can_run(){
        ONode cfg = node_cfg();

        if(cfg == null){
            _interval = 1000 * 60;
            System.out.println(getName()+"::is not enabled");
            return false;
        }

        if(cfg.contains("task") && cfg.get("task").getString().indexOf(getName())<0){
            _interval = 1000 * 60;
            System.out.println(getName()+"::is not enabled");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public int getInterval() {
        return _interval;
    }
}
