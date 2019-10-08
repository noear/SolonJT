package org.noear.solonjt.dso;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solonjt.utils.StringUtils;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.XUtil;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;
import org.noear.weed.cache.LocalCache;

import java.util.Map;


/*
 * 数据库处理工具
 *
 * 启动参数示例：-server.port=8081 -extend=/data/sss/tk_ext/
 * */
public class DbUtil {
    /** 说明：
     *
     * 为启动时配一下连接信息
     * */
    public static final LocalCache cache = new LocalCache("data", 60*5);
    private static DbContext _db = null;

    public static DbContext db(){
        return _db;
    }

    public static void setDefDb(XMap map) {
        _db = getDb(map);
    }


    private static DbContext getDb(String db, String driver, String type, String url,String server, String usr, String pwd) {
        if (TextUtils.isEmpty(url)) {
            StringBuilder sb = StringUtils.borrowBuilder();
            sb.append("jdbc:").append(type).append("://")
                    .append(server)
                    .append("/")
                    .append(db);

            if("mysql".equals(type)) {
                sb.append("?useSSL=false&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true");
            }
            url = StringUtils.releaseBuilder(sb);
        }

        if(TextUtils.isEmpty(driver)==false) {
            try {
                Class.forName(driver);
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        }

        if("mysql".equals(type)){
            HikariDataSource source = new HikariDataSource();
            source.setJdbcUrl(url);
            source.setUsername(usr);
            source.setPassword(pwd);
            source.setSchema(db);
            if(TextUtils.isEmpty(driver)==false) {
                source.setDriverClassName(driver);
            }

            return new DbContext(db, source);
        }else{
            return new DbContext(db, url,usr,pwd);
        }

        //return new DbContext(db, url,usr,pwd, null);
    }

    public static DbContext getDb(Map<String,String> map) {
        String url = map.get("url");
        String server = map.get("server");
        String db = map.get("name");
        String usr = map.get("username");
        if(TextUtils.isEmpty(usr)){
            usr = map.get("usr");
        }
        String pwd = map.get("password");
        if(TextUtils.isEmpty(pwd)){
            pwd = map.get("pwd");
        }
        String type = map.get("type");
        if(TextUtils.isEmpty(type)){
            type="mysql";
        }

        String driver = map.get("driver");

        if ((XUtil.isEmpty(url) && XUtil.isEmpty(server)) || XUtil.isEmpty(db) || XUtil.isEmpty(usr) || XUtil.isEmpty(pwd)) {
            throw new RuntimeException("please enter a normal database config");
        }

        return getDb(db, driver, type, url, server, usr, pwd);
    }
}
