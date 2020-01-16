package org.noear.solonjt.model;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.snack.ONode;
import org.noear.solon.core.XMap;
import org.noear.solonjt.utils.RunUtil;
import org.noear.solonjt.utils.TextUtils;
import org.noear.weed.DbContext;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class AConfigM {
    public final String value;
    public AConfigM(String value){
        this.value = value;
    }


    public String getString() {
        return value;
    }

    public String getString(String def) {
        return value == null ? def : value;
    }

    /**
     * 转为Int
     */
    public int getInt(int def) {
        if (TextUtils.isEmpty(value)) {
            return def;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 转为Long
     */
    public long getLong(long def) {
        if (TextUtils.isEmpty(value)) {
            return def;
        } else {
            return Long.parseLong(value);
        }
    }

    public XMap getXmap(){
        return XMap.from(value.split("\\n"));
    }

    /**
     * 转为Properties
     */
    private Properties _prop;

    public Properties getProp() {
        if (_prop == null) {
            _prop = new Properties();
            RunUtil.runActEx(() -> _prop.load(new StringReader(value)));
        }

        return _prop;
    }

    /**
     * 转为ONode
     */
    private ONode _node;

    public ONode getNode() {
        if (_node == null) {
            _node = ONode.load(value);
        }

        return _node;
    }

    /**
     * 获取 db:DbContext
     */
    private Map<String,DbContext> _dbMap = new ConcurrentHashMap<>();
    public DbContext getDb() {
        return getDb(false);
    }

    public DbContext getDb(boolean pool) {
        if(TextUtils.isEmpty(value)){
            return null;
        }

        DbContext db = _dbMap.get(value);
        if(db == null){
            db = getDbDo(pool);
            _dbMap.putIfAbsent(value,db);
        }
        return db;
    }

    private DbContext getDbDo(boolean pool) {
        Properties prop = getProp();
        String url = prop.getProperty("url");

        if(TextUtils.isEmpty(url)){
            return null;
        }


        DbContext db = new DbContext();

        if (pool) {
            HikariDataSource source = new HikariDataSource();

            String schema = prop.getProperty("schema");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String driverClassName = prop.getProperty("driverClassName");

            if (TextUtils.isEmpty(url) == false) {
                source.setJdbcUrl(url);
            }

            if (TextUtils.isEmpty(username) == false) {
                source.setUsername(username);
            }

            if (TextUtils.isEmpty(password) == false) {
                source.setPassword(password);
            }

            if (TextUtils.isEmpty(schema) == false) {
                source.setSchema(schema);
            }

            if (TextUtils.isEmpty(driverClassName) == false) {
                source.setDriverClassName(driverClassName);
            }

            db.dataSourceSet(source);
            db.schemaSet(schema);
        } else {
            db.propSet(getProp());
        }

        return db;
    }
}
