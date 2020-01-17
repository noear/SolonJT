package org.noear.solonjt.utils;

import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.noear.solon.annotation.XNote;
import org.noear.weed.ext.Fun0;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    private final static Fun0<Dispatcher> okhttp_dispatcher = ()->{
        Dispatcher temp = new Dispatcher();
        temp.setMaxRequests(3000);
        temp.setMaxRequestsPerHost(600);
        return temp;
    };

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60*5, TimeUnit.SECONDS)
            .writeTimeout(60*5, TimeUnit.SECONDS)
            .readTimeout(60*5, TimeUnit.SECONDS)
            .dispatcher(okhttp_dispatcher.run())
            .build();

    public static HttpUtils http(String url){
        return new HttpUtils(url);
    }

    private String _url;
    private Charset _charset;
    private Map<String,String> _cookies;
    private RequestBody _body;
    private Map<String,String> _form;
    private MultipartBody.Builder _part_builer;

    private Request.Builder _builder;
    public HttpUtils(String url){
        _url = url;
        _builder = new Request.Builder().url(url);
    }

    @XNote("设置UA")
    public HttpUtils userAgent(String ua){
        _builder.header("User-Agent", ua);
        return this;
    }

    @XNote("设置charset")
    public HttpUtils charset(String charset){
        _charset = Charset.forName(charset);
        return this;
    }

    @XNote("设置请求头")
    public HttpUtils headers(Map<String,Object> headers){
        if (headers != null) {
            headers.forEach((k, v) -> {
                _builder.header(k, v.toString());
            });
        }

        return this;
    }

    @XNote("设置请求头")
    public HttpUtils header(String name, String value) {
        if(name!=null) {
            _builder.header(name, value);
        }
        return this;
    }

    @XNote("设置数据提交")
    public HttpUtils data(Map<String,Object> data){
        tryInitForm();

        if (data != null) {
            data.forEach((k, v) -> {
                _form.put(k, v.toString());
            });
        }

        return this;
    }

    @XNote("设置数据提交")
    public HttpUtils data(String key, String value){
        tryInitForm();
        _form.put(key,value);
        return this;
    }

    @XNote("设置表单文件提交")
    @Deprecated
    public HttpUtils data(String key, String filename, InputStream inputStream, String contentType){
        return part(key,filename,inputStream,contentType);
    }


    @XNote("设置表单数据提交")
    public HttpUtils part(Map<String,Object> data){
        tryInitPartBuilder(MultipartBody.FORM);

        if (data != null) {
            data.forEach((k, v) -> {
                _part_builer.addFormDataPart(k, v.toString());
            });
        }

        return this;
    }

    @XNote("设置表单数据提交")
    public HttpUtils part(String key, String value) {
        tryInitPartBuilder(MultipartBody.FORM);
        _part_builer.addFormDataPart(key,value);
        return this;
    }


    @XNote("设置表单文件提交")
    public HttpUtils part(String key, String filename, InputStream inputStream, String contentType) {
        tryInitPartBuilder(MultipartBody.FORM);

        _part_builer.addFormDataPart(key,
                filename,
                new StreamBody(contentType,inputStream) );

        return this;
    }

    @XNote("设置BODY提交")
    public HttpUtils bodyTxt(String txt, String contentType){
        if(contentType == null) {
            _body = FormBody.create(null, txt);
        }else{
            _body = FormBody.create(MediaType.parse(contentType), txt);
        }

        return this;
    }
    @XNote("设置BODY提交")
    public HttpUtils bodyRaw(InputStream raw, String contentType) {
        if(contentType == null) {
            _body = new StreamBody(null, raw);
        }else{
            _body = new StreamBody(contentType, raw);
        }

        return this;
    }


    @XNote("设置请求cookies")
    public HttpUtils cookies(Map<String,Object> cookies){
        if (cookies != null) {
            tryInitCookies();

            cookies.forEach((k, v) -> {
                _cookies.put(k, v.toString());
            });
        }

        return this;
    }

    @XNote("执行请求，返回响应对象")
    public Response exec(String mothod) throws Exception {
        if (_part_builer != null) {
            if (_form != null) {
                _form.forEach((k, v) -> {
                    _part_builer.addFormDataPart(k, v);
                });
            }
            try {
                _body = _part_builer.build();
            }catch (IllegalStateException ex){

            }
        } else {
            if (_form != null) {
                FormBody.Builder fb = new FormBody.Builder(_charset);

                _form.forEach((k, v) -> {
                    fb.add(k, v);
                });
                _body = fb.build();
            }
        }

        if (_cookies != null) {
            _builder.header("Cookie", getRequestCookieString(_cookies));
        }



        switch (mothod.toUpperCase()){
            case "GET":_builder.method("GET",null);break;
            case "POST":_builder.method("POST",bodyDo());break;
            case "PUT":_builder.method("PUT", bodyDo());break;
            case "DELETE":_builder.method("DELETE",bodyDo());break;
            case "PATCH":_builder.method("PATCH",bodyDo());break;
            case "HEAD":_builder.method("HEAD",null);break;
            case "OPTIONS":_builder.method("OPTIONS",null);break;
            case "TRACE":_builder.method("TRACE",null);break;
            default: throw new RuntimeException("This method is not supported");
        }

        Call call = httpClient.newCall(_builder.build());
        return call.execute();
    }

    private RequestBody bodyDo(){
        if(_body ==null){
            _body = RequestBody.create(null, "");
        }

        return _body;
    }

    @XNote("执行请求，返回字符串")
    public String exec2(String mothod) throws Exception {
        Response tmp = exec(mothod);
        int code = tmp.code();
        String text = tmp.body().string();
        if (code >= 200 && code <= 300) {
            return text;
        } else {
            throw new RuntimeException(code + "错误：" + text);
        }
    }

    @XNote("发起GET请求，返回字符串（REST.select 从服务端获取一或多项资源）")
    public String get() throws Exception{
        return exec2("GET");
    }

    @XNote("发起POST请求，返回字符串（REST.create 在服务端新建一项资源）")
    public String post() throws Exception{
        return exec2("POST");
    }

    @XNote("发起PUT请求，返回字符串（REST.update 客户端提供改变后的完整资源）")
    public String put() throws Exception {
        return exec2("PUT");
    }

    @XNote("发起PATCH请求，返回字符串（REST.update 客户端提供改变的属性）")
    public String patch() throws Exception {
        return exec2("PATCH");
    }

    @XNote("发起DELETE请求，返回字符串（REST.delete 从服务端删除资源）")
    public String delete() throws Exception {
        return exec2("DELETE");
    }

    private static String getRequestCookieString(Map<String,String> cookies) {
        StringBuilder sb = StringUtils.borrowBuilder();
        boolean first = true;

        for(Map.Entry<String,String> kv : cookies.entrySet()){
            sb.append(kv.getKey()).append('=').append(kv.getValue());
            if (!first) {
                sb.append("; ");
            } else {
                first = false;
            }
        }

        return StringUtils.releaseBuilder(sb);
    }

    private void tryInitPartBuilder(MediaType type){
        if(_part_builer == null){
            _part_builer = new MultipartBody.Builder().setType(type);
        }
    }

    private void tryInitForm(){
        if(_form ==null){
            _form = new HashMap<>();
        }
    }

    private void tryInitCookies(){
        if(_cookies==null){
            _cookies = new HashMap<>();
        }
    }

    public static class StreamBody extends RequestBody{
        private  MediaType _contentType = null;
        private InputStream _inputStream = null;
        public StreamBody(String contentType, InputStream inputStream) {
            if (contentType != null) {
                _contentType = MediaType.parse(contentType);
            }

            _inputStream = inputStream;
        }
        @Override
        public MediaType contentType() {
            return _contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return _inputStream.available();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;

            try {
                source = Okio.source(_inputStream);
                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }

}
