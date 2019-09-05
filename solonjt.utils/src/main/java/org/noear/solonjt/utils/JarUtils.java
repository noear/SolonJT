package org.noear.solonjt.utils;

import org.noear.solon.XApp;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.XPlugin;

import java.io.File;
import java.io.FileOutputStream;

public class JarUtils {
    public static boolean loadJar(String path, String data64,String xPlugin) throws Exception {
        String extend = XApp.global().prop().argx().get("extend");

        byte[] data = Base64Utils.decodeByte(data64);
        int idx = path.lastIndexOf('/');
        String filename = extend + path.substring(idx);

        File file = new File(filename);
        if (file.exists()) {
            //检测md5
            byte[] data2 = IOUtils.getFileBytes(file);
            String md1 = EncryptUtils.md5Bytes(data);
            String md2 = EncryptUtils.md5Bytes(data2);

            //如果一样，则不需要更新
            if (md1 != null && md1.equals(md2)) {
                return false;
            }

            //否则删掉文件，重新创建
            file.deleteOnExit();
        }

        //创建jar文件并写入
        file.createNewFile();

        FileOutputStream fw = new FileOutputStream(file);
        fw.write(data);
        fw.close();

        //加载jar包
        try {
            ExtendLoader.loadJar(file);

            //尝试加载插件类
            if (TextUtils.isEmpty(xPlugin) == false) {
                XPlugin p1 = newClass(xPlugin);
                if (p1 != null) {
                    XApp.global().plug(p1);
                    return false;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return true;
    }

    public static <T> T newClass(String className) {
        try {
            Class clz = Class.forName(className);
            return (T) clz.newInstance();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
