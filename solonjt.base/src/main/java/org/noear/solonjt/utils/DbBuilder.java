package org.noear.solonjt.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.weed.DbContext;
import org.noear.weed.wrap.DbType;

import java.util.Map;

public class DbBuilder {
    private static Boolean _useConnectionPool;

    //是否使用连接池
    public static boolean useConnectionPool() {
        if (_useConnectionPool == null) {
            _useConnectionPool = ("0".equals(XApp.cfg().get("solonjt.connection.pool")) == false);
        }

        return _useConnectionPool;
    }

    public static DbContext getDb(Map<String, String> map) {
        String url = map.get("url");
        if(TextUtils.isEmpty(url) == false) {
            if (url.indexOf("~/") >= 0) {
                String path = XApp.cfg().argx().get("extend");
                url = url.replace("~/", path);
            }
        }


        String server = map.get("server");

        String schema = map.get("schema");
        if (TextUtils.isEmpty(schema)) {
            schema = map.get("name");
        }

        String username = map.get("username");
        if (TextUtils.isEmpty(username)) {
            username = map.get("usr");
        }
        String password = map.get("password");
        if (TextUtils.isEmpty(password)) {
            password = map.get("pwd");
        }
        String type = map.get("type");

        String driverClassName = map.get("driverClassName");
        if (TextUtils.isEmpty(driverClassName)) {
            driverClassName = map.get("driver");
        }

        if ((XUtil.isEmpty(url) && XUtil.isEmpty(server))
                || XUtil.isEmpty(schema)
                || XUtil.isEmpty(username)
                || XUtil.isEmpty(password)) {
            throw new RuntimeException("please enter a normal database config");
        }

        return getDb(schema, driverClassName, type, url, server, username, password);
    }

    private static DbContext getDb(String schema, String driverClassName, String type, String url, String server, String username, String password) {
        if (TextUtils.isEmpty(type)) {
            type = "mysql";
        } else {
            type = type.trim();
        }

        if (schema != null) {
            schema = schema.trim();
        }

        if (driverClassName != null) {
            driverClassName = driverClassName.trim();
        }

        if (schema != null) {
            schema = schema.trim();
        }

        if (url != null) {
            url = url.trim();
        }

        if (server != null) {
            server = server.trim();
        }

        if (schema != null) {
            schema = schema.trim();
        }

        if (username != null) {
            username = username.trim();
        }

        if (password != null) {
            password = password.trim();
        }

        if (TextUtils.isEmpty(url)) {
            StringBuilder sb = StringUtils.borrowBuilder();
            sb.append("jdbc:").append(type).append("://")
                    .append(server.trim())
                    .append("/")
                    .append(schema.trim());

            if ("mysql".equals(type)) {
                sb.append("?useSSL=false&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true");
            }

            url = StringUtils.releaseBuilder(sb);
        }

        if (TextUtils.isEmpty(driverClassName) == false) {
            try {
                Class.forName(driverClassName);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        DbContext db = null;

        if (useConnectionPool()) {
            HikariDataSource source = new HikariDataSource();
            source.setJdbcUrl(url);
            source.setUsername(username);
            source.setPassword(password);

            if (TextUtils.isEmpty(schema) == false) {
                source.setSchema(schema);
            }

            if (TextUtils.isEmpty(driverClassName) == false) {
                source.setDriverClassName(driverClassName);
            }

            db = new DbContext(schema, source);
        } else {
            db = new DbContext(schema, url, username, password);
        }

        if (db.dbType() == DbType.H2) {
            db.dbAdapterSet(DbH2AdapterEx.g);
        }

        return db;
    }
}
