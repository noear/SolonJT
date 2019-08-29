package org.noear.solonjt.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.noear.solon.annotation.XNote;

import java.io.InputStream;
import java.util.Map;

public class HttpUtils {
    private Connection _con;
    public HttpUtils(String url){
        _con = Jsoup.connect(url)
                .ignoreContentType(true)
                .maxBodySize(Integer.MAX_VALUE);
    }

    @XNote("设置UA")
    public HttpUtils userAgent(String ua){
        _con.userAgent(ua);
        return this;
    }

    @XNote("设置charset")
    public HttpUtils charset(String charset){
        _con.postDataCharset(charset);
        return this;
    }

    @XNote("设置请求头")
    public HttpUtils headers(Map<String,Object> headers){
        if (headers != null) {
            headers.forEach((k, v) -> {
                _con.header(k, v.toString());
            });
        }

        return this;
    }

    @XNote("设置数据提交")
    public HttpUtils data(Map<String,Object> data){
        if (data != null) {
            data.forEach((k, v) -> {
                _con.data(k, v.toString());
            });
        }

        return this;
    }

    @XNote("设置文件提交")
    public HttpUtils file(String key, String filename, InputStream inputStream) {
        _con.data(key,filename,inputStream);
        return this;
    }

    @XNote("设置文件提交")
    public HttpUtils file(String key, String filename, InputStream inputStream, String contentType) {
        _con.data(key,filename,inputStream,contentType);
        return this;
    }

    @XNote("设置BODY提交")
    public HttpUtils body(String str){
        _con.requestBody(str);
        return this;
    }

    @XNote("设置请求cookies")
    public HttpUtils cookies(Map<String,Object> cookies){
        if (cookies != null) {
            cookies.forEach((k, v) -> {
                _con.cookie(k, v.toString());
            });
        }

        return this;
    }

    @XNote("执行请求，返回响应对象")
    public Connection.Response exec(String mothod) throws Exception{
        switch (mothod.toUpperCase()){
            case "GET":_con.method(Connection.Method.GET);break;
            case "POST":_con.method(Connection.Method.POST);break;
            case "PUT":_con.method(Connection.Method.PUT);break;
            case "DELETE":_con.method(Connection.Method.DELETE);break;
            case "PATCH":_con.method(Connection.Method.PATCH);break;
            case "HEAD":_con.method(Connection.Method.HEAD);break;
            case "OPTIONS":_con.method(Connection.Method.OPTIONS);break;
            case "TRACE":_con.method(Connection.Method.TRACE);break;
            default: throw new RuntimeException("This method is not supported");
        }

        return _con.execute();
    }

    @XNote("发起get请求，返回字符串")
    public String get() throws Exception{
        return _con.method(Connection.Method.GET).execute()
                .body();
    }

    @XNote("发起post请求，返回字符串")
    public String post() throws Exception{
        return _con.method(Connection.Method.POST).execute()
                .body();
    }

    @XNote("发起put请求，返回字符串")
    public String put() throws Exception{
        return _con.method(Connection.Method.PUT).execute()
                .body();
    }

}
