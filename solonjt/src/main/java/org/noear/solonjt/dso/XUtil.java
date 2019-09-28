package org.noear.solonjt.dso;

import net.coobird.thumbnailator.Thumbnails;
import org.noear.solonjt.Config;
import org.noear.solonjt.controller.FrmInterceptor;
import org.noear.solonjt.controller.SufHandler;
import org.noear.solonjt.executor.ExecutorFactory;
import org.noear.solonjt.model.AImageModel;
import org.noear.solonjt.utils.*;
import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XNote;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 引擎扩展工具，提供一些基础的操作支持
 * */
public class XUtil {
    public static final XUtil g = new XUtil();

    private final Map<String,DbContext> _db_cache = new HashMap<>();

    /**
     * 生成GUID
     */
    @XNote("生成GUID")
    public String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    @XNote("获取当前用户IP")
    public String ip() {
        return IPUtils.getIP(XContext.current());
    }

    /**
     * 生成数据库上下文
     */
    @XNote("生成数据库上下文")
    public DbContext db(String cfg) {
        return db(cfg,null);
    }

    @XNote("生成数据库上下文")
    public DbContext db(String cfg, DbContext def) {
        if (TextUtils.isEmpty(cfg)) {
            return def;
        }

        DbContext tmp = _db_cache.get(cfg);

        if (tmp == null) {
            synchronized (cfg) {
                tmp = _db_cache.get(cfg);

                if (tmp == null) {
                    List<String> args = new ArrayList<>();
                    String[] strs = cfg.split(" |\n|\r|\t");
                    for(int i=0,len=strs.length; i<len; i++){
                        if(strs[i].length()>0){
                            args.add(strs[i]);
                        }
                    }

                    tmp = DbUtil.getDb(XMap.from(args));

                    if (tmp != null) {
                        _db_cache.put(cfg, tmp);
                    }
                }
            }
        }

        return tmp;
    }

    /**
     * 空的只读集合
     * */
    private final Map<String, Object> _empMap = Collections.unmodifiableMap(new HashMap<>());
    @XNote("空的Map<String,Object>集合")
    public Map<String, Object> empMap() {
        return _empMap;
    }


    private List<Object> _empList = Collections.unmodifiableList(new ArrayList<>());
    @XNote("空的List<Object>集合")
    public List<Object> empList() {
        return _empList;
    }

    private Set<Object> _empSet = Collections.unmodifiableSet(new HashSet<>());
    @XNote("创建一个Set<Object>集合")
    public Set<Object> empSet() {
        return _empSet;
    }


    /**
     * 新建集合
     * */
    @XNote("创建一个Map<String,Object>集合")
    public Map<String, Object> newMap() {
        return new HashMap<>();
    }


    @XNote("创建一个List<Object>集合")
    public List<Object> newList() {
        return new ArrayList<>();
    }

    @XNote("创建一个List<Object>集合")
    public List<Object> newList(Object[] ary) {
        return Arrays.asList(ary);
    }

    @XNote("创建一个Set<Object>集合")
    public Set<Object> newSet() {
        return new HashSet<>();
    }

    @XNote("创建一个ByteArrayOutputStream空对象")
    public OutputStream newOutputStream(){
        return new ByteArrayOutputStream();
    }

    @XNote("创建一个XFile空对象")
    public XFile newXfile(){
        return new XFile();
    }

    @XNote("执行 HTTP 请求")
    public HttpUtils http(String url) {
        return new HttpUtils(url);
    }


    @XNote("编码html")
    public String htmlEncode(String str) {
        if (str == null) {
            return "";
        } else {
            str = str.replaceAll("<", "&lt;");
            str = str.replaceAll(">", "&gt;");
        }
        return str;
    }

    @XNote("编码url")
    public String urlEncode(String str) throws Exception{
        if(str == null){
            return str;
        }

       return URLEncoder.encode(str, "utf-8");
    }

    @XNote("解码url")
    public String urlDecode(String str) throws Exception{
        if(str == null){
            return str;
        }

        return URLDecoder.decode(str, "utf-8");
    }

    /** ****************************/

    /**
     * 配置获取
     */
    @XNote("配置获取")
    public String cfgGet(String name) throws Exception {
        return DbApi.cfgGet(name);
    }

    @XNote("配置获取")
    public String cfgGet(String name,String def) throws Exception {
        return DbApi.cfgGet(name, def);
    }

    /**
     * 配置设置
     */
    @XNote("配置设置")
    public boolean cfgSet(String name, String value) throws Exception {
        return DbApi.cfgSet(name, value, null);
    }

    public List<Map<String,Object>> menuGet(String label, int pid) throws Exception{
        return DbApi.menuGet(label, pid);
    }

    /**
     * 保存图片
     */
    @XNote("保存图片")
    public String imgSet(XFile file) throws Exception {
        return imgSet(file, file.extension);
    }

    @XNote("保存图片")
    public String imgSet(XFile file, String tag, String dir, int name_mod) throws Exception {
        String extension = file.extension;
        byte[] data_byte = IOUtils.toBytes(file.content);
        String data = Base64Utils.encodeByte(data_byte);
        StringBuilder path = StringUtils.borrowBuilder();

        if(name_mod==0) {
            //自动
            tag = null;
            path.append("/img/");
            path.append(guid());
            path.append(".");
            path.append(extension);
        }else{
            //保持原名
            path.append("/img/");

            if(TextUtils.isEmpty(tag)==false){
                path.append(tag).append("/");
            }

            if(TextUtils.isEmpty(dir)==false){
                path.append(dir).append("/");
            }

            path.append(file.name);
        }

        String path2= StringUtils.releaseBuilder(path).replace("//", "/");

        DbApi.imgSet(tag, path2, file.contentType, data, "");

        return path2;
    }

    /**
     * 保存图片（后缀名可自定义）
     */
    @XNote("保存图片（后缀名可自定义）")
    public String imgSet(XFile file, String extension) throws Exception {
        byte[] data_byte = IOUtils.toBytes(file.content);
        String data = Base64Utils.encodeByte(data_byte);
        String path = "/img/" + guid() + "." + extension;

        DbApi.imgSet(null,path, file.contentType, data, "");

        return path;
    }

    /**
     * 保存图片（内容，类型，后缀名可自定义）
     */
    @XNote("保存图片（内容，类型，后缀名可自定义）")
    public String imgSet(String content, String contentType, String extension) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + guid() + "." + extension;

        DbApi.imgSet(null,path, contentType, data, "");

        return path;
    }

    @XNote("保存图片（内容，类型，名字，后缀名可自定义）")
    public String imgSet(String content, String contentType, String extension, String name) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + name + "." + extension;

        DbApi.imgSet(null,path, contentType, data, "");

        return path;
    }

    @XNote("保存图片（内容，类型，名字，后缀名，标签可自定义）")
    public String imgSet(String content, String contentType, String extension, String name, String label) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + name + "." + extension;

        DbApi.imgSet(null, path, contentType, data, label);

        return path;
    }


    @XNote("设定图片输出名称")
    public String imgOutName(XContext ctx, String filename) throws Exception {
        String filename2 = URLEncoder.encode(filename, "utf-8");

        ctx.headerSet("Content-Disposition", "attachment; filename=\"" + filename2 + "\"");
        return filename;
    }

    /**
     * 修改图片
     */
    @XNote("修改图片")
    public String imgUpd(String path, String content) throws Exception {
        String data = Base64Utils.encode(content);

        DbApi.imgUpd(path, data);

        return path;
    }

    /**
     * 获取图片内容
     */
    @XNote("获取图片内容(string)")
    public String imgGet(String path) throws Exception {
        AImageModel img = DbApi.imgGet(path);
        return img2String(img.data);
    }

    @XNote("获取图片内容(byte[])")
    public byte[] imgGetBytes(String path) throws Exception {
        AImageModel img = DbApi.imgGet(path);

        if (TextUtils.isEmpty(img.data)) {
            return null;
        } else {
            return Base64Utils.decodeByte(img.data);
        }

    }


    @XNote("图片内容转为字符串")
    public String img2String(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        } else {
            return Base64Utils.decode(data);
        }
    }

    /**
     * 新建文件
     */
    @XNote("文件新建")
    public boolean fileNew(int fid, XContext ctx) throws Exception {
        return DbApi.fileNew(fid, ctx);
    }

    /**
     * 文件设置内容
     */
    @XNote("文件设置内容")
    public boolean fileSet(int fid, String fcontent) throws Exception {
        return DbApi.fileSet(fid, fcontent);
    }

    @XNote("文件刷新缓存")
    public boolean fileFlush(String path, boolean is_del) {
        if(TextUtils.isEmpty(path)){
            return false;
        }

        String name = path.replace("/", "__");

        if(is_del){
            RouteHelper.del(path);
        }else{
            RouteHelper.add(path);
        }

        AFileUtil.remove(path);
        ExecutorFactory.del(name);
        return true;
    }

    @XNote("文件刷新缓存")
    public boolean fileFlush(String path, boolean is_del, String label, String note) {
        if(TextUtils.isEmpty(path)){
            return false;
        }

        String path2 = path;
        String name = path2.replace("/", "__");

        AFileUtil.remove(path2);
        ExecutorFactory.del(name);

        //应用路由
        if(is_del){
            RouteHelper.del(path);
        }else{
            RouteHelper.add(path);
        }

        //后缀拦截器
        if(Config.filter_file.equals(label)){
            if(is_del){
                SufHandler.g().del(note);//文件后缀，只能有一个代理；所以用suf
            }else{
                SufHandler.g().add(path,note);
            }
        }

        //路径拦截器
        if(Config.filter_path.equals(label)){
            if(is_del){
                FrmInterceptor.g().del(path);//文件路径，可以有多个；所以用path
            }else{
                FrmInterceptor.g().add(path,note);
            }
        }

        return true;
    }

    @XNote("重启缓存")
    public boolean restart() {
        AFileUtil.removeAll();
        AImageUtil.removeAll();

        ExecutorFactory.delAll();

        DbUtil.cache.clear();

        SufHandler.g().reset();
        FrmInterceptor.g().reset();

        RouteHelper.reset();
        return true;
    }

    /**
     *
     ****************************/
    @XNote("获取接口开放清单")
    public List<Map<String, Object>> interfaceList() {
        Map<String, Object> tmp = new HashMap<>();

        tmp.putAll(XApp.global().shared());
        tmp.put("XUtil.http(url)", HttpUtils.class);
        tmp.put("XUtil.db(cfg)", DbContext.class);
        tmp.put("XUtil.paging(ctx,pageSize)", PagingModel.class);
        tmp.put("XUtil.thumbnailOf(stream)", Thumbnails.Builder.class);

        tmp.put("ctx", XContext.class);

        tmp.put("XFile", XFile.class);

        tmp.put("new Datetime()", Datetime.class);
        tmp.put("new Timecount()", Timecount.class);
        tmp.put("new Timespan(date)", Timespan.class);

        List<Map<String, Object>> list = MethodUtils.getMethods(tmp);

        XFun.g.openList(list);

        //排序
        Collections.sort(list, Comparator.comparing(m -> m.get("name").toString().toLowerCase()));

        return list;
    }

    @XNote("获取执行器清单")
    public Set<String> executorList(){
        return ExecutorFactory.list();
    }

    @XNote("添加共享对象（key, 以 _ 开头）")
    public boolean sharedAdd(String key, Object obj){
        if(TextUtils.isEmpty(key)){
            return false;
        }

        if(key.startsWith("_")) {
            XApp.global().sharedAdd(key, obj);
            return true;
        }else{
            return false;
        }
    }

    @XNote("检查共享对象")
    public boolean sharedHas(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        return XApp.global().shared().containsKey(key);
    }

    @XNote("获取扩展目录下的文件")
    public List<Map<String,Object>> extendList(){
        return ExtendUtil.scan();
    }

    @XNote("删除扩展目录下的文件")
    public boolean extendDel(String name){
        return ExtendUtil.del(name);
    }



    @XNote("生成md5码")
    public String md5(String str) {
        return EncryptUtils.md5(str, "UTF-16LE");
    }

    @XNote("生成md5码")
    public String md5(String str, String charset) {
        return EncryptUtils.md5(str, charset);
    }


    @XNote("生成sha1码")
    public String sha1(String str) {
        return EncryptUtils.sha1(str, "UTF-16LE");//UTF-16LE, utf-8
    }

    @XNote("生成sha1码")
    public String sha1(String str, String charset) {
        return EncryptUtils.sha1(str, charset);
    }


    /**
     * base64
     */
    @XNote("BASE64编码")
    public String base64Encode(String text) {
        return Base64Utils.encode(text);
    }

    @XNote("BASE64解码")
    public String base64Decode(String text) {
        return Base64Utils.decode(text);
    }

    /**
     * 生成随机码
     */
    @XNote("生成随机码")
    public String codeByRandom(int len) {
        return TextUtils.codeByRandom(len);
    }

    /**
     * 字符码转为图片
     */
    @XNote("字符码转为图片")
    public BufferedImage codeToImage(String code) throws Exception {
        return ImageUtils.getValidationImage(code);
    }

    @XNote("InputStream转为String")
    public String streamToString(InputStream inStream) throws Exception {
        return IOUtils.toString(inStream, "utf-8");
    }

    @XNote("OutStream转为InputStream")
    public InputStream streamOutToIn(OutputStream outStream) throws Exception
    {
        return IOUtils.outToIn(outStream);
    }

    @XNote("String转为InputStream")
    public InputStream stringToStream(String str) throws Exception{
        return new ByteArrayInputStream(str.getBytes("UTF-8"));
    }

    @XNote("Object转为ONode")
    public ONode oNode(Object obj) throws Exception {
        if(obj == null){
            return new ONode();
        }else {
            if(obj instanceof String){
                String tmp = ((String) obj).trim();

                if(tmp.length()==0){
                    return new ONode();
                }

                if(tmp.startsWith("{")){
                    return ONode.fromStr(tmp);
                }
            }

            return ONode.fromObj(obj);
        }
    }

    @XNote("生成分页数据模型")
    public PagingModel paging(XContext ctx, int pageSize) {
        return new PagingModel(ctx, pageSize, false);
    }

    @XNote("生成分页数据模型")
    public PagingModel paging(XContext ctx, int pageSize, boolean fixedSize) {
        return new PagingModel(ctx, pageSize, fixedSize);
    }

    @XNote("格式化活动时间")
    public String liveTime(Date date) {
        return TimeUtils.liveTime(date);
    }

    @XNote("创建缩略图工具")
    public Object thumbnailOf(InputStream stream){
        return Thumbnails.of(stream);
    }


    @XNote("是否为数字")
    public boolean isNumber(String str) {
        return TextUtils.isNumber(str);
    }

    @XNote("加载插件里的jar包")
    public boolean loadJar(String path, String data64, String data_md5, String plugin) throws Exception {
        return JarUtil.loadJar(path,data64,data_md5,plugin);
    }

    @XNote("调用一个文件")
    public Object callFile(String path) throws Exception {
        return CallUtil.callFile(path ,null);
    }

    @XNote("调用一个文件")
    public Object callFile(String path, Map<String,Object> attrs) throws Exception {
        return CallUtil.callFile(path, attrs);
    }

    @XNote("调用一组勾子")
    public String callHook(String tag, String label, boolean useCache) throws Exception{
        return CallUtil.callLabel(tag, label, useCache, null);
    }

    @XNote("调用一组勾子")
    public String callHook(String tag,String label, boolean useCache, Map<String,Object> attrs) throws Exception{
        return CallUtil.callLabel(tag, label, useCache, attrs);
    }

    @XNote("日志")
    public boolean log(Map<String,Object> data) throws Exception{
        return LogUtil.log(data);
    }

    private String _localAddr;
    @XNote("服务地址")
    public String localAddr() throws Exception{
        if(_localAddr == null) {
            _localAddr = LocalUtil.getLocalAddress(XApp.global().port());
        }

        return _localAddr;
    }

    @XNote("设置上下文状态（用于在模板中停止请求）")
    public int statusSet(int status) throws Exception{
        XContext.current().status(status);
        throw new RuntimeException(status+" status");
    }

    @XNote("将对象转为string")
    public String stringOf(Object obj){
        if(obj == null){
            return null;
        }

        if(obj instanceof Throwable){
            return ExceptionUtils.getString((Throwable)obj);
        }

        return obj.toString();
    }
}
