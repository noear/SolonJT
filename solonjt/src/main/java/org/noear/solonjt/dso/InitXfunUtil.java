package org.noear.solonjt.dso;

public class InitXfunUtil {
    public static void init(){

        XFun.g.set("log","记录日志#tag,tag1?,tag2?,tag3?,tag4?,level?,summary?,content?,from?",
                DbApi::log);
        XFun.g.set("cfg_get","获取配置#name#{}",1,
                DbApi::cfgGetMap);

        XFun.g.set("afile_get","获取文件#path#AfileModel",1,
                (map)-> AFileUtil.get((String) map.get("path")));
        XFun.g.set("afile_get_paths","获取文件路径#tag,label,useCache#AfileModel",1,
                (map)-> DbApi.fileGetPaths((String) map.get("tag"), (String) map.get("label"),(Boolean) map.get("useCache")));
    }
}