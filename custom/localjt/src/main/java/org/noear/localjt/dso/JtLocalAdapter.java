package org.noear.localjt.dso;

import org.noear.solon.XApp;
import org.noear.solonjt.dso.JtAdapter;
import org.noear.solonjt.model.AFileModel;
import org.noear.solonjt.utils.TextUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JtLocalAdapter extends JtAdapter {
    static Map<String,AFileModel> localMap = new HashMap<>();
    static String extend;
    static AFileModel empFile = new AFileModel();

    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel tmp = super.fileGet(path);
        if (tmp.file_id > 0) {
            return tmp;
        }

        return fileGetByLocal(path);
    }

    @Override
    public AFileModel fileGetByLocal(String path) throws Exception {
        AFileModel tmp = localMap.get(path);
        if (tmp == null) {
            tmp = fileGetByLocalDo(path);
            localMap.put(path, tmp);
        }

        return tmp;
    }

    private static AFileModel fileGetByLocalDo(String path) throws Exception{
        if(TextUtils.isEmpty(path)){
            return empFile;
        }

        if (XApp.global() == null) {
            return empFile;
        }

        if (extend == null) {
            extend = XApp.cfg().argx().get("extend") + "/code";
        }

        File file = new File(extend + path);
        if(file.exists()){
            AFileModel af = new AFileModel();
            af.file_id = -1;
            af.path = path;
            af.content = stream2String(new FileInputStream(file));

            if(path.endsWith(".js")){
                af.edit_mode = "javascript";
            } else if(path.endsWith(".htm")){
                af.is_staticize = true;
                af.edit_mode = "html";
            } else{
                af.edit_mode = "ftl";
            }

            return af;
        }

        return empFile;
    }

    private static final String stream2String(InputStream stream) throws IOException {
        stream.reset();
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toString();
    }
}
