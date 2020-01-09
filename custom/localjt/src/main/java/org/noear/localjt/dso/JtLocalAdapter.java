package org.noear.localjt.dso;

import org.noear.solon.XApp;
import org.noear.solonjt.dso.JtAdapter;
import org.noear.solonjt.model.AFileModel;

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

        if (XApp.global() == null) {
            return tmp;
        }

        if (extend == null) {
            extend = XApp.cfg().argx().get("extend") + "/code";
        }

        File file = new File(extend + path);
        if(file.exists()){
            AFileModel af = new AFileModel();
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

        return tmp;
    }

    public static final String stream2String(InputStream stream) throws IOException {
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
