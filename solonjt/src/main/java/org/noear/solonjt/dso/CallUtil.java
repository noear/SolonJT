package org.noear.solonjt.dso;

import org.noear.solonjt.engine.EngineFactory;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;
import org.noear.solon.core.XContext;
import org.noear.solonjt.utils.ThreadUtils;

public class CallUtil {

    private static Object do_call(String path, boolean outString) throws Exception {
        String path2 = path;//不用转换*
        String name = path2.replace("/", "__");

        AFileModel file = AFileUtil.get(path2);

        if (file.file_id > 0 && TextUtils.isEmpty(file.content) == false) {
            return EngineFactory.call(name, file, XContext.current(), null, outString);
        } else {
            return "";
        }
    }

    /**
     * 调用文件，返回对象
     * */
    public static Object callFile(String path) throws Exception {
        if(TextUtils.isEmpty(path)){
            return null;
        }

        return do_call(path,false);
    }

    /**
     * 调用勾子，返回字符串。勾子调用不能出错，以免影响主业务
     */
    public static String callHook(String tag,String label, boolean useCache) {
        if(TextUtils.isEmpty(tag) && TextUtils.isEmpty(label)){
            return "";
        }

        StringBuilder sb = ThreadUtils.getStringBuilder();

        try {
            DbApi.fileGetPaths(tag,label,useCache)
                    .forEach((f) -> {
                        try {
                            Object tmp = do_call(f.path, true);
                            if (tmp != null) {
                                sb.append(tmp.toString()).append("\r\n");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return sb.toString();
    }
}
